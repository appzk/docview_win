package com.idocv.docview.service.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.idocv.docview.dao.CacheDao;
import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.DocPo;
import com.idocv.docview.service.ConvertService;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.MemoryUtil;
import com.idocv.docview.util.ProcessUtil;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.MemoryVo;

@Service
public class ConvertServiceImpl implements ConvertService {

	private static final Logger logger = LoggerFactory.getLogger(ConvertServiceImpl.class);
	
	private static final ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	public static final int processorCount = Runtime.getRuntime().availableProcessors();
	
	public static final BlockingQueue<String> convertQueue = new ArrayBlockingQueue<String>(100000);

	public static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
	
	public static long uploadRate = 0;

	public static boolean SYSTEM_LOAD_HIGH = false;
	
	public static DateFormat dfMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static Map<String, Integer> lastMinuteCountMap = new HashMap<String, Integer>();

	@Resource
	private DocDao docDao;

	@Resource
	private CacheDao cacheDao;

	@Resource
	private DocService docService;

	@Resource
	private ViewService viewService;

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
				int emptyCheckCount = 0;
				int convertingDocCount = 0;
				while (true) {
					if (convertQueue.isEmpty()) {
						emptyCheckCount++;
						logger.debug("[CONVERT] convert queue is EMPTY, triggering batch convert...");
						startBatchConvert();
						try {
							Thread.sleep(convertBatchInterval + (2000 * emptyCheckCount));
						} catch (Exception e) {
							logger.error("[CONVERT] convert thread sleep error: " + e.getMessage());
						}
						continue;
					}
					if (SYSTEM_LOAD_HIGH) {
						try {
							logger.info("[CONVERT] system load is high, waiting for 0.5 minutes...");
							Thread.sleep(30000);
						} catch (Exception e) {
							logger.error("[CONVERT] convert thread(high load waiting) sleep error: " + e.getMessage());
						}
						continue;
					}

					if (convertingDocCount > 20) {
						try {
							Map<Integer, String> curRunningProcessMap = ProcessUtil.getProcessByNameList(ProcessUtil.serviceNameList);
							convertingDocCount = curRunningProcessMap.size();
							if (curRunningProcessMap.size() > (5 * processorCount)) {
								logger.warn("[SYSTEM LOAD] PROCESSOR(" + processorCount + ") is processing " + curRunningProcessMap.size() + " docs(" + curRunningProcessMap + ")");
								SYSTEM_LOAD_HIGH = true;
								continue;
							}
						} catch (Exception e) {
							logger.error("[CONVERT]" + e.getMessage());
						}
					}

					emptyCheckCount = 0;
					try {
						String rid = convertQueue.take();
						logger.info("[CONVERT] start converting(" + rid + ") from convert queue(" + (convertQueue.size() + 1) + ")");
						ConvertService convertService = new ConvertServiceImpl(viewService, rid);
						es.submit(convertService);
						convertingDocCount++;
						Thread.sleep(100);
					} catch (Exception e) {
						logger.error("[CONVERT] take RID from convert queue error: " + e.getMessage());
					}
				}
			};
		}.start();
	}

	public ConvertServiceImpl(ViewService previewService, String rid) {
		this.viewService = previewService;
		this.rid = rid;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			viewService.convert(rid);
		} catch (Exception e) {
			logger.warn("[CONVERT] convert(" + rid + ") task fail: " + e.getMessage());
			return false;
		}
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

		// check last minute upload frequency
		String currentMinute = dfMinute.format(new Date());
		Integer lastMinuteCount = lastMinuteCountMap.get(currentMinute);
		if (null == lastMinuteCount) {
			lastMinuteCountMap.clear();
			lastMinuteCountMap.put(currentMinute, 1);
		} else if (lastMinuteCount < convertSwitchThresholdUploadFrequency) {
			lastMinuteCount++;
			lastMinuteCountMap.put(currentMinute, lastMinuteCount);
		} else {
			// Load is high
			SYSTEM_LOAD_HIGH = true;
			return;
		}

		if (SYSTEM_LOAD_HIGH) {
			return;
		}

		ConvertService convertService = new ConvertServiceImpl(viewService, rid);
		es.submit(convertService);
		return;
	}

	@Scheduled(cron = "${convert.switch.check.system.load.cron}")
	public void checkSystemLoad() {
		// check upload frequency within last five minutes
		boolean isHighLoad = false;
		try {
			long now = System.currentTimeMillis();
			long fiveMinutesBack = now - 300000;
			int countOfFiveMinutes = docDao.countAppDocs(null, null, null, 0, fiveMinutesBack, now);
			uploadRate = countOfFiveMinutes / 5;
			if (uploadRate < convertSwitchThresholdUploadFrequency) {
				// logger.info("[SYSTEM LOAD] upload rate: " + uploadRate + "(" + countOfFiveMinutes + "/" + "5)/m");
			} else {
				isHighLoad = true;
				logger.warn("[SYSTEM LOAD] upload rate: " + uploadRate + "(" + countOfFiveMinutes + "/" + "5)/m");
			}
		} catch (Exception e) {
			logger.error("[SYSTEM LOAD] check upload frequency error: " + e.getMessage());
			return;
		}
		
		// check heap memory usage
		try {
			MemoryVo heapVo = MemoryUtil.getHeapMemoryInfo();
			MemoryVo systemVo = MemoryUtil.getSystemMemoryInfo();
			if (heapVo.getRate() < convertSwitchThresholdMemoryUsage && systemVo.getFree() > 200000000) {
				logger.info("[SYSTEM LOAD] memory info - HEAP(init|used|max): " + heapVo.getRate() + "(" + heapVo.getMin() + "|" + heapVo.getUsed() + "|" + heapVo.getMax() + ") - SYSTEM(used|free|total): " + systemVo.getRate() + "(" + systemVo.getUsed() + "|" + systemVo.getFree() + "|" + systemVo.getMax() + ")");
			} else {
				isHighLoad = true;
				logger.warn("[SYSTEM LOAD] memory info - HEAP(init|used|max): " + heapVo.getRate() + "(" + heapVo.getMin() + "|" + heapVo.getUsed() + "|" + heapVo.getMax() + ") - SYSTEM(used|free|total): " + systemVo.getRate() + "(" + systemVo.getUsed() + "|" + systemVo.getFree() + "|" + systemVo.getMax() + ")");
			}
		} catch (Exception e) {
			logger.error("[SYSTEM LOAD] check memeory usage error: " + e.getMessage());
			return;
		}
		
		// converting proccess
		try {
			Map<Integer, String> curRunningProcessMap = ProcessUtil.getProcessByNameList(ProcessUtil.serviceNameList);
			if (curRunningProcessMap.size() > (5 * processorCount)) {
				logger.warn("[SYSTEM LOAD] PROCESSOR(" + processorCount + ") is processing " + curRunningProcessMap.size() + " docs(" + curRunningProcessMap + ")");
				isHighLoad = true;
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// check CPU usage
		// TODO

		if (isHighLoad) {
			SYSTEM_LOAD_HIGH = true;
			return;
		}
		SYSTEM_LOAD_HIGH = false;

		// check converting queue
		if (!convertQueue.isEmpty()) {
			return;
		}

		// delete error files
		try {
			List<String> convertErrorIds = docDao.listDocIdsConvertError(null, 100);
			if (!CollectionUtils.isEmpty(convertErrorIds)) {
				logger.warn("[DELETING CONVERT ERROR DOCS] " + convertErrorIds);
				for (String convertErrorId : convertErrorIds) {
					String uuid = RcUtil.getUuidByRid(convertErrorId);
					docService.delete(uuid, false);
				}
			}
		} catch (Exception e) {
			logger.error("delete error converted docs error: " + e.getMessage());
		}

		startBatchConvert();
	}

	private void startBatchConvert() {
		if (!SYSTEM_LOAD_HIGH) {
			// get batch docs
			try {
				String batchConvertStart = cacheDao.getGlobal("batchConvertStart");
				List<String> rids = docDao.listDocIdsNotConverted(batchConvertStart, convertBatchSize);
				if (CollectionUtils.isEmpty(rids)) {
					return;
				}

				// set batch convert time point
				String lastRid = rids.get(rids.size() - 1);
				DocPo lastDoc = docDao.get(lastRid, false);
				String lastDocCtime = lastDoc.getCtime();
				cacheDao.setGlobal("batchConvertStart", lastDocCtime);

				logger.info("[CONVERT] adding " + rids.size() + " docs to convert queue...");
				convertQueue.addAll(rids);
			} catch (Exception e) {
				logger.error("[CONVERT] list doc IDs NOT converted error: " + e.getMessage());
				return;
			}
		}
	}
}