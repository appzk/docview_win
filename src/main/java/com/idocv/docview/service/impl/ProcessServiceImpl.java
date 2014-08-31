package com.idocv.docview.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.idocv.docview.util.ProcessUtil;

@Service
public class ProcessServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(ProcessServiceImpl.class);
	
	// running processes. pid->service name
	public static Map<Integer, String> lastRunningProcessMap = new HashMap<Integer, String>();

	@Scheduled(fixedRate = 600000)
	public void cleanupZombieProcesses() {
		try {
			// 1. Kill previous processes
			if (!lastRunningProcessMap.isEmpty()) {
				logger.warn("Last running processes: " + lastRunningProcessMap);
				ProcessUtil.killProcessByPIDs(lastRunningProcessMap.keySet());
				Thread.sleep(1000);
				lastRunningProcessMap.clear();
			}

			// 2. Get current processes
			Map<Integer, String> curRunningProcessMap = ProcessUtil.getProcessByNameList(ProcessUtil.serviceNameList);
			if (null != curRunningProcessMap && !curRunningProcessMap.isEmpty()) {
				lastRunningProcessMap.putAll(curRunningProcessMap);
			}
		} catch (Exception e) {
			logger.error("[IDOCV] Cleanup zombie process error: " + e.getMessage());
		}
	}

}