package com.idocv.test;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

public class Test {
	public static void main(String[] args) {
		try {
			String url = "http://115.28.1.229/Uploads/雨天.xls";
			// url = "http://www.21dm.cn/api/20130916工作安排.xls";
			String host = getHost(url);
			System.out.println("host: " + host);
			Response urlResponse = Jsoup.connect(url).referrer(host).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreContentType(true).execute();
			byte[] bytes = urlResponse.bodyAsBytes();
			System.out.println("size: " + bytes.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getHost(String url) {
		return url.replaceFirst("((http[s]?)?(://))?([^/]*)(/?.*)", "$4");
	}
}
