package com.idocv.docview.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.idocv.docview.service.DocService;

@Service
public class DataCleanupServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(DataCleanupServiceImpl.class);
	
	@Resource
	private DocService docService;

	@Value("${data.auto.cleanup.switch}")
	private boolean dataAutoCleanupSwitch;

	@Value("${data.max.keep.days}")
	private int dataMaxKeepDays;

	@Scheduled(cron = "${data.auto.cleanup.cron}")
	public void cleanup() {
		if (!dataAutoCleanupSwitch) {
			return;
		}
		logger.info("[AUTO DATA CLEANUP START]");
		Date now = new Date();
		Date startDate = DateUtils.addYears(now, -3);
		Date endDate = DateUtils.addDays(now, -dataMaxKeepDays - 1);
		try {
			docService.deleteByDateRange(startDate, endDate, false);
		} catch (Exception e) {
			logger.warn("[AUTO CLEANUP DATA ERROR] " + e.getMessage());
		}
	}

}