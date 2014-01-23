package com.idocv.docview.service.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.ConvertService;
import com.idocv.docview.service.ViewService;

@Service
public class ConvertServiceImpl implements ConvertService {

	private static final Logger logger = LoggerFactory.getLogger(ConvertServiceImpl.class);
	
	private static final ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	public static final int cpuCount = Runtime.getRuntime().availableProcessors();
	
	public static final BlockingQueue<String> convertQueue = new ArrayBlockingQueue<String>(100000);

	public static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
	
	public static long uploadRate = 0;

	public static boolean SYSTEM_LOAD_HIGH = false;
	
	@Resource
	private DocDao docDao;

	@Resource
	private ViewService previewService;

	private String rid;
	
	@Value("${convert.switch.mode}")
	private int convertSwitchMode;

	@Value("${convert.switch.threshold.upload.frequency}")
	private int convertSwitchThresholdUploadFrequency;

	@Value("${convert.switch.threshold.memory.usage}")
	private double convertSwitchThresholdMemoryUsage;
	
	@Value("${convert.batch.size}")
	private int convertBatchSize;

	@Value("${convert.batch.interval}")
	private int convertBatchInterval;

	public ConvertServiceImpl() {
		new Thread() {
			public void run() {
				int emptyCheckCount = 1;
				while (true) {
					if (convertQueue.isEmpty()) {
						emptyCheckCount++;
						logger.info("[CONVERT] convert queue is EMPTY, triggering batch convert...");
						startBatchConvert();
						try {
							Thread.sleep(10000 * emptyCheckCount);
						} catch (Exception e) {
							logger.error("[CONVERT] convert thread sleep error: " + e.getMessage());
						}
						continue;
					}
					emptyCheckCount = 1;
					int count = 0;
					try {
						String rid = convertQueue.take();
						logger.info("[CONVERT] start converting(" + rid + ") from convert queue(" + convertQueue.size() + ")");
						ConvertService convertService = new ConvertServiceImpl(previewService, rid);
						es.submit(convertService);
						count++;
						if (count >= cpuCount) {
							count = 0;
							Thread.sleep(convertBatchInterval);
						}
					} catch (Exception e) {
						logger.error("[CONVERT] take RID from convert queue error: " + e.getMessage());
					}
				}
			};
		}.start();
	}

	public ConvertServiceImpl(ViewService previewService, String rid) {
		this.previewService = previewService;
		this.rid = rid;
	}

	@Override
	public Boolean call() throws Exception {
		previewService.convert(rid);
		return true;
	}


	@Override
	public void convert(String rid) throws DocServiceException {
		if (convertSwitchMode <= 0) {
			return;
		}
		if (convertSwitchMode == 1 && SYSTEM_LOAD_HIGH) {
			return;
		}
		ConvertService convertService = new ConvertServiceImpl(previewService, rid);
		es.submit(convertService);
		return;
	}

	@Scheduled(cron = "${convert.switch.check.system.load.cron}")
	public void checkSystemLoad() {
		// check upload frequency within last five minutes
		try {
			long now = System.currentTimeMillis();
			long fiveMinutesBack = now - 300000;
			int countOfFiveMinutes = docDao.countAppDocs(null, null, null, 0, fiveMinutesBack, now);
			uploadRate = countOfFiveMinutes / 5;
			logger.info("[SYSTEM LOAD] upload rate: " + uploadRate + "(" + countOfFiveMinutes + "/" + "5)/m");
			if (uploadRate >= convertSwitchThresholdUploadFrequency) {
				SYSTEM_LOAD_HIGH = true;
				logger.warn("[SYSTEM LOAD] upload rate: " + uploadRate + "(" + countOfFiveMinutes + "/" + "5)/m");
				return;
			}
		} catch (Exception e) {
			logger.error("[SYSTEM LOAD] check upload frequency error: " + e.getMessage());
			return;
		}
		
		// check memory usage
		try {
			MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
			long init = memoryUsage.getInit();
			long used = memoryUsage.getUsed();
			long max = memoryUsage.getMax();
			double memoryRate = (double) used / max;
			memoryRate = new BigDecimal(memoryRate).setScale(2, RoundingMode.HALF_UP).doubleValue();
			logger.info("[SYSTEM LOAD] memory rate: " + memoryRate + "(" + init + "|" + used + "|" + max + ")");
			if (memoryRate >= convertSwitchThresholdMemoryUsage) {
				SYSTEM_LOAD_HIGH = true;
				logger.warn("[SYSTEM LOAD] memory rate: " + memoryRate + "(" + init + "|" + used + "|" + max + ")");
				return;
			}
		} catch (Exception e) {
			logger.error("[SYSTEM LOAD] check memeory usage error: " + e.getMessage());
			return;
		}

		// check CPU usage
		// TODO

		SYSTEM_LOAD_HIGH = false;

		// check converting queue
		if (!convertQueue.isEmpty()) {
			return;
		}

		startBatchConvert();
	}

	private void startBatchConvert() {
		if (!SYSTEM_LOAD_HIGH) {
			// get batch docs
			try {
				List<String> rids = docDao.listDocIdsNotConverted(convertBatchSize);
				if (CollectionUtils.isEmpty(rids)) {
					return;
				}
				logger.info("[CONVERT] adding " + rids.size() + " docs to convert queue...");
				convertQueue.addAll(rids);
			} catch (Exception e) {
				logger.error("[CONVERT] list doc IDs NOT converted error: " + e.getMessage());
				return;
			}
		}
	}
}