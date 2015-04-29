package com.idocv.docview.vo;

import org.apache.commons.lang.builder.ToStringBuilder;

public class MemoryVo {
	private double rate;
	private long min;
	private long max;
	private long free;
	private long used;

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public long getFree() {
		return free;
	}

	public void setFree(long free) {
		this.free = free;
	}

	public long getUsed() {
		return used;
	}

	public void setUsed(long used) {
		this.used = used;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}