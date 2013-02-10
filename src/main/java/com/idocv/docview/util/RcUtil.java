package com.idocv.docview.util;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component("rcUtil")
public class RcUtil {

	private @Value("${docview.data.url}")
	String dataUrl;

	private @Value("${docview.data.dir}")
	String dataDir;

	private static final String SPLIT = "_";

	/**
	 * 生成rid，格式：(appId)_(yyyyMMdd)_(HHmmss)(size)(random)_ext
	 * 
	 * @param uid
	 * @param fileName
	 * @param size
	 * @return
	 */
	public static String genRid(String appId, String fileName, int size) {
		Date date = new Date();
		String dateString = new SimpleDateFormat("yyyyMMdd").format(date);
		String timeString = new SimpleDateFormat("HHmmss").format(date);
		String randomString = RandomStringUtils.randomAlphanumeric(3);
		return appId + SPLIT + dateString + SPLIT + timeString + size + randomString + SPLIT + fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
	}

	/**
	 * 根据rid获取绝对路径，e.g. /dataDir/coff180e/20130118/2314324_abc.doc
	 * 
	 * @param rid
	 * @return
	 * @throws IllegalArgumentException
	 */
	public String getPath(String rid) throws IllegalArgumentException {
		validateRid(rid);
		return getDirectoryByRid(rid) + getFileNameByRid(rid);
	}

	public static String getFileNameByRid(String rid) throws IllegalArgumentException{
		validateRid(rid);
		String[] splits = rid.split(SPLIT);
		return splits[splits.length - 2] + "." + splits[splits.length - 1];
	}
	
	public static void validateRid(String rid) throws IllegalArgumentException {
		if (StringUtils.isBlank(rid) || !rid.matches("\\w{1,}_\\d{8}_\\w{1,}_\\w{1,}")) {
			throw new IllegalArgumentException("Invalid rid.");
		}
	}
	
	public String getParsePathOfHtml(String rid) {
		String nameWithoutExt = RcUtil.getFileNameWithoutExt(rid);
		String dir = getDirectoryByRid(rid) + nameWithoutExt;
		if (!new File(dir).isDirectory()) {
			new File(dir).mkdirs();
		}
		return dir + File.separator + "index.html";
	}
	
	/**
	 * 解析文档的URL目录
	 * 
	 * @param rid
	 * @return
	 */
	public String getParseUrlDir(String rid) {
		String nameWithoutExt = RcUtil.getFileNameWithoutExt(rid);
		String dir = dataUrl + RcUtil.getDirectoryWithoutRootByRid(rid) + nameWithoutExt + File.separator;
		return dir;
	}
	
	/**
	 * 获取解析文档的本地目录
	 * 
	 * @param rid
	 * @return
	 */
	public String getParseDir(String rid) {
		String nameWithoutExt = RcUtil.getFileNameWithoutExt(rid);
		String dir = dataDir + RcUtil.getDirectoryWithoutRootByRid(rid) + nameWithoutExt + File.separator;
		if (!new File(dir).isDirectory()) {
			new File(dir).mkdirs();
		}
		return dir;
	}
	
	public String getDirectoryByRid(String rid) throws IllegalArgumentException {
		validateRid(rid);
		File dir = new File(dataDir + getDirectoryWithoutRootByRid(rid));
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		return dataDir + getDirectoryWithoutRootByRid(rid);
	}
	
	public static String getDirectoryWithoutRootByRid(String rid) throws IllegalArgumentException {
		validateRid(rid);
		String[] splits = rid.split(SPLIT);
		String yyyy = splits[1].substring(0, 4);
		String mmdd = splits[1].substring(4, 8);
		return splits[0] + File.separator + yyyy + File.separator + mmdd + File.separator;
	}

	public static String getExt(String rid) throws IllegalArgumentException {
		validateRid(rid);
		String[] splits = rid.split(SPLIT);
		return splits[splits.length - 1].toLowerCase();
	}

	public static String getFileNameWithoutExt(String rid) throws IllegalArgumentException {
		validateRid(rid);
		String[] splits = rid.split(SPLIT);
		return splits[2];
	}
}