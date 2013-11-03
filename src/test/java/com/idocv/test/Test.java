package com.idocv.test;


public class Test {
	public static void main(String[] args) {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getHost(String url) {
		return url.replaceFirst("((http[s]?)?(://))?([^/]*)(/?.*)", "$4");
	}
}
