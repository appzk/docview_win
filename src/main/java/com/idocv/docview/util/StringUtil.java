package com.idocv.docview.util;

import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

public class StringUtil {

	public static String removeParam(String queryString, String name) {
		if (null == queryString || !queryString.contains(name)) {
			return queryString;
		}
		String[] paraArry = queryString.split("&");
		String newStr = "";
		for (String para : paraArry) {
			if (!para.startsWith(name + "=")) {
				newStr += "&" + para;
			}
		}
		return newStr.startsWith("&") ? newStr.substring(1) : newStr;
	}

	public static String urlencode(String queryString) {
		if (null == queryString) {
			return queryString;
		}
		String[] paraArry = queryString.split("&");
		String newStr = "";
		String regex = "([^=]+)=(.*)";
		for (String para : paraArry) {
			try {
				if (para.matches(regex)) {
					String key = para.replaceFirst(regex, "$1");
					String value = para.replaceFirst(regex, "$2");
					newStr += "&" + key + "="
							+ URLEncoder.encode(value, "UTF-8");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return StringUtils.isBlank(newStr) ? newStr : newStr.substring(1);
	}

}