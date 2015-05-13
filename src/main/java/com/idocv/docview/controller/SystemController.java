package com.idocv.docview.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
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
	
	private @Value("${data.url}")
	String dataUrl;

	@ResponseBody
	@RequestMapping("conf.json")
	public Map<Object, Object> confJson() {
		Map<Object, Object> params = new HashMap<Object, Object>();
		try {
			Resource resource = new ClassPathResource("/conf.properties");
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			for (Entry<Object, Object> entry : props.entrySet()) {
				System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
				params.put(entry.getKey(), entry.getValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return params;
	}

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
		return "system/cd";
	}

	@ResponseBody
	@RequestMapping("cmd.json")
	public Map<String, String> cmdJson(@RequestParam(value = "cmd", required = false) String cmd) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			if (StringUtils.isNotBlank(dataUrl) && dataUrl.contains("idocv.com")) {
				result.put("code", "0");
				result.put("msg", "This action is FORBIDDEN under I Doc View.");
				return result;
			}
			Process proc = Runtime.getRuntime().exec("cmd /c " + cmd);
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(proc.getInputStream(), "gbk"));
			StringBuffer sb = new StringBuffer();
			String line;
			sb.append("<br />&diams;&bull;&bull;&bull;&gt;&gt;&gt; START &lt;&lt;&lt; " + cmd + " &gt;&gt;&gt;&gt;&gt;&gt;&gt;<br />");
			while ((line = bufferedreader.readLine()) != null) {
				sb.append(StringEscapeUtils.escapeHtml(line) + "<br />");
			}
			sb.append("&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;&lt;END");
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