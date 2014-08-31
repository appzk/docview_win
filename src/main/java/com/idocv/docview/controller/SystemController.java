package com.idocv.docview.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.service.impl.ConvertServiceImpl;
import com.idocv.docview.util.ProcessUtil;
import com.sun.management.OperatingSystemMXBean;

@Controller
@RequestMapping("system")
public class SystemController {
	
	private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

	@ResponseBody
	@RequestMapping("info.json")
	public Map<String, Object> infoJson() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpuCount", ConvertServiceImpl.cpuCount);
		params.put("queueSize", ConvertServiceImpl.convertQueue.size());
		params.put("highLoad", ConvertServiceImpl.SYSTEM_LOAD_HIGH);

		// JVM
		MemoryUsage memoryUsage = ConvertServiceImpl.memoryMXBean.getHeapMemoryUsage();
		long memoryInit = memoryUsage.getInit();
		long memoryUsed = memoryUsage.getUsed();
		long memoryMax = memoryUsage.getMax();
		params.put("memInit", memoryInit);
		params.put("memUsed", memoryUsed);
		params.put("memMax", memoryMax);
		double memoryRate = (double) memoryUsed / memoryMax;
		memoryRate = new BigDecimal(memoryRate).setScale(2, RoundingMode.HALF_UP).doubleValue();
		params.put("memRate", memoryRate);
		params.put("uploadAvg", ConvertServiceImpl.uploadRate);
		
		// Operating system
		String os = System.getProperty("os.name");
		OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		long physicalFree = osmxb.getFreePhysicalMemorySize();
		long physicalTotal = osmxb.getTotalPhysicalMemorySize();
		long physicalUse = physicalTotal - physicalFree;
		params.put("os", os);
		params.put("osMemTotal", physicalTotal);
		params.put("osMemUsed", physicalUse);
		params.put("osMemFree", physicalFree);
		return params;
	}
	
	@ResponseBody
	@RequestMapping("process.json")
	public Map<Integer, String> process(
			@RequestParam(value = "filter", required = false) String filter) {
		try {
			if ("idocv".equals(filter)) {
				return ProcessUtil.getProcessByNameList(ProcessUtil.serviceNameList);
			} else {
				return ProcessUtil.getAllProcessesWithPid();
			}
		} catch (Exception e) {
			logger.error("Get system process error: " + e.getMessage());
			Map<Integer, String> map = new HashMap<Integer, String>();
			map.put(-1, e.getMessage());
			return map;
		}
	}

	@RequestMapping("cmd")
	public String cmd() {
		return "system/cmd";
	}

	@ResponseBody
	@RequestMapping("cmd.json")
	public Map<String, String> cmdJson(@RequestParam(value = "cmd", required = false) String cmd) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			Process proc = Runtime.getRuntime().exec("cmd /c " + cmd);
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(proc.getInputStream(), "gbk"));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = bufferedreader.readLine()) != null) {
				sb.append(StringEscapeUtils.escapeHtml(line) + "<br />");
			}
			bufferedreader.close();
			result.put("code", "1");
			result.put("data", sb.toString());
		} catch (Exception e) {
			result.put("code", "0");
			result.put("msg", e.getMessage());
		}
		return result;
	}

	@RequestMapping("info")
	public String info() {
		return "system/info";
	}

	@RequestMapping("memory")
	public String memory() {
		return "system/memory";
	}
}