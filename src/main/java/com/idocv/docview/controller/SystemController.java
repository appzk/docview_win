package com.idocv.docview.controller;

import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.service.impl.ConvertServiceImpl;

@Controller
@RequestMapping("system")
public class SystemController {

	@ResponseBody
	@RequestMapping("info.json")
	public Map<String, Object> infoJson() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cpuCount", ConvertServiceImpl.cpuCount);
		params.put("queueSize", ConvertServiceImpl.convertQueue.size());
		params.put("highLoad", ConvertServiceImpl.SYSTEM_LOAD_HIGH);
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
		return params;
	}

	@RequestMapping("info")
	public String info() {
		return "system/info";
	}
}