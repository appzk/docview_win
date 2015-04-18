package com.idocv.docview.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.idocv.docview.service.impl.ConvertServiceImpl;
import com.idocv.docview.vo.MemoryVo;
import com.sun.management.OperatingSystemMXBean;

public class MemoryUtil {
	
	private static MemoryUsage memoryUsage = ConvertServiceImpl.memoryMXBean.getHeapMemoryUsage();
	private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	
	public static MemoryVo getHeapMemoryInfo() {
		// JVM
		long memoryInit = memoryUsage.getInit();
		long memoryUsed = memoryUsage.getUsed();
		long memoryMax = memoryUsage.getMax();
		double memoryRate = (double) memoryUsed / memoryMax;
		memoryRate = new BigDecimal(memoryRate).setScale(2, RoundingMode.HALF_UP).doubleValue();
		
		MemoryVo vo = new MemoryVo();
		vo.setRate(memoryRate);
		vo.setMin(memoryInit);
		vo.setUsed(memoryUsed);
		vo.setMax(memoryMax);
		return vo;
	}
	
	public static MemoryVo getSystemMemoryInfo() {
		// Operating system
		// System memory
		String os = System.getProperty("os.name");
		OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		long physicalFree = osmxb.getFreePhysicalMemorySize();
		long physicalTotal = osmxb.getTotalPhysicalMemorySize();
		long physicalUse = physicalTotal - physicalFree;
		double memoryRateSystem = (double) physicalUse / physicalTotal;
		memoryRateSystem = new BigDecimal(memoryRateSystem).setScale(2, RoundingMode.HALF_UP).doubleValue();
		
		MemoryVo vo = new MemoryVo();
		vo.setRate(memoryRateSystem);
		vo.setUsed(physicalUse);
		vo.setFree(physicalFree);
		vo.setMax(physicalTotal);
		return vo;
	}

	public static void main(String[] args) {
		System.out.println("Heap:\n" + getHeapMemoryInfo());
		System.out.println("System:\n" + getSystemMemoryInfo());
	}
}