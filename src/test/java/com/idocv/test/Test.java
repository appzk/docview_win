package com.idocv.test;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Test {
	public static void main(String[] args) {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		while (true) {
			MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
			long init = memoryUsage.getInit();
			long used = memoryUsage.getUsed();
			long max = memoryUsage.getMax();
			double memoryRate = (double) used / max;
			memoryRate = new BigDecimal(memoryRate).setScale(2,
					RoundingMode.HALF_UP).doubleValue();
			System.out.println("memory rate: " + memoryRate + ", init: " + init
					+ ", used: " + used + ", max: " + max);
			try {
				Thread.sleep(2000);
				byte[] data = new byte[10240];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}