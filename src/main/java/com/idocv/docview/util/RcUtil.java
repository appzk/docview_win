package com.idocv.docview.util;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.idocv.docview.common.IpUtil;


@Component("rcUtil")
public class RcUtil {

	private @Value("${docview.data.url}")
	String dataUrl;

	private @Value("${docview.data.dir}")
	String dataDir;

	private static final String SPLIT = "_";
	private final static DateFormat yyyymmddformat = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 生成rid，格式：<8位加密ip>_<yyyyMMddHHmmss>_<size>_<random>_ext
	 * 
	 * @param uid
	 * @param fileName
	 * @param size
	 * @return
	 */
	public static String genRid(String ip, String fileName, int size) {
		String encodedIp = IpUtil.encodeIp(ip);
		Date date = new Date();
		String dateString = new SimpleDateFormat("yyyyMMdd").format(date);
		String timeString = new SimpleDateFormat("HHmmss").format(date);
		String randomString = RandomStringUtils.randomAlphanumeric(3);
		return encodedIp + SPLIT + dateString + SPLIT + timeString + size + SPLIT + randomString + SPLIT + fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
	}

	/**
	 * 根据rid获取绝对路径，e.g. /dataDir/coff180e/20130118/2314324_abc.doc
	 * 
	 * @param rid
	 * @return
	 * @throws IllegalArgumentException
	 */
	public String getPath(String rid) throws IllegalArgumentException {
		if (!isValidRid(rid)) {
			throw new IllegalArgumentException("Invalid rid.");
		}
		return getDirectoryByRid(rid) + getFileNameByRid(rid);
	}

	public static String getFileNameByRid(String rid) throws IllegalArgumentException{
		if (!isValidRid(rid)) {
			throw new IllegalArgumentException("Invalid rid.");
		}
		String[] splits = rid.split(SPLIT);
		return splits[2] + SPLIT + splits[3] + "." + splits[4];
	}
	
	public static boolean isValidRid(String rid) {
		if (StringUtils.isNotBlank(rid) && rid.matches("\\w{8}_\\d{8}_\\d{1,}_\\w{3}_.*")) {
			return true;
		} else {
			return false;
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
		if (!isValidRid(rid)) {
			throw new IllegalArgumentException("Invalid rid.");
		}
		File dir = new File(dataDir + getDirectoryWithoutRootByRid(rid));
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		return dataDir + getDirectoryWithoutRootByRid(rid);
	}
	
	public static String getDirectoryWithoutRootByRid(String rid) throws IllegalArgumentException {
		if (!isValidRid(rid)) {
			throw new IllegalArgumentException("Invalid rid.");
		}
		String[] splits = rid.split(SPLIT);
		return splits[0] + File.separator + splits[1] + File.separator;
	}

	public static String getExt(String rid) throws IllegalArgumentException {
		if (!isValidRid(rid)) {
			throw new IllegalArgumentException("Invalid rid.");
		}
		String[] splits = rid.split(SPLIT);
		return splits[4];
	}

	public static String getFileNameWithoutExt(String rid) throws IllegalArgumentException {
		if (!isValidRid(rid)) {
			throw new IllegalArgumentException("Invalid rid.");
		}
		String[] splits = rid.split(SPLIT);
		return splits[2] + SPLIT + splits[3];
	}
}