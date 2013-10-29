package com.idocv.docview.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

public class UrlUtil {
	public static void main(String[] args) {
		String url = "http://121.199.59.128/Uploads/测试文件/word测试文件/word文档.doc";
		String s = "http://121.199.59.128/Uploads/%E6%B5%8B%E8%AF%95%E6%96%87%E4%BB%B6/word%E6%B5%8B%E8%AF%95%E6%96%87%E4%BB%B6/word%E6%96%87%E6%A1%A3.doc";
		System.out.println(s);
		System.out.println(encodeUrl(url));
	}

	public static String encodeUrl(String url) {
		if (StringUtils.isBlank(url)) {
			return "";
		}
		char[] chars = url.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (char c : chars) {
			if (!isChinese(c)) {
				sb.append(c);
			} else {
				try {
					sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}
}