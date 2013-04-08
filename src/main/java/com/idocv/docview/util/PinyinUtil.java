package com.idocv.docview.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {

	private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

	static {
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	}

	/**
	 * get one character PINYIN
	 * 
	 * @param c
	 * @return
	 */
	public static String getCharacterPinYin(char c) {
		try {
			String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
			if (null == pinyin) {
				return null;
			} else {
				return pinyin[0];
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			return null;
		}
	}

	/**
	 * get string PINYIN.
	 * 
	 * @param str
	 * @return
	 */
	public static String getPinYin(String str) {
		if (null == str || "".equals(str.trim())) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			String pinyin = getCharacterPinYin(str.charAt(i));
			if (null != pinyin && 0 == i) {
				sb.append("~");
			}
			sb.append(null == pinyin ? str.charAt(i) : pinyin);
		}
		return sb.toString();
	}

	/**
	 * get string PINYIN, if the first character is Chinese, it will prepend a
	 * '~' sign.
	 * 
	 * @param str
	 * @return
	 */
	public static String getSortPinYin(String str) {
		if (null == str || "".equals(str.trim())) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			String pinyin = getCharacterPinYin(str.charAt(i));
			if (null != pinyin && 0 == i) {
				sb.append("~");
			}
			sb.append(null == pinyin ? str.charAt(i) : pinyin);
		}
		return sb.toString().toLowerCase();
	}

}
