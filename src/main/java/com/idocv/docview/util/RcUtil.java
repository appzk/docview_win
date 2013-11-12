package com.idocv.docview.util;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.ViewService;


@Component("rcUtil")
public class RcUtil {

	private @Value("${docview.data.url}")
	String dataUrl;

	private @Value("${docview.data.dir}")
	String dataDir;

	private static final String SPLIT = "_";

	/**
	 * 生成rid，格式：(appId)_(yyyyMMdd)_(HHmmss)_(size)_(uuid)_ext
	 * 
	 * @param uid
	 * @param fileName
	 * @param size
	 * @return
	 */
	public static String genRid(String appId, String fileName, int size) throws DocServiceException {
		if (StringUtils.isBlank(appId)) {
			throw new DocServiceException("应用id为空！");
		}
		if (StringUtils.isBlank(fileName)) {
			throw new DocServiceException("文件名为空！");
		}
		if (size <= 0) {
			throw new DocServiceException("文件大小为0！");
		}
		Date date = new Date();
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		String uuid = RandomStringUtils.randomAlphabetic(6);
		if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)) {
			uuid += "w";
		} else if ("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext)) {
			uuid += "x";
		} else if ("ppt".equalsIgnoreCase(ext) || "pptx".equalsIgnoreCase(ext)) {
			uuid += "p";
		} else if ("pdf".equalsIgnoreCase(ext)) {
			uuid += "f";
		} else if ("txt".equalsIgnoreCase(ext)) {
			uuid += "t";
		}
		String dateString = new SimpleDateFormat("yyyyMMdd").format(date);
		String timeString = new SimpleDateFormat("HHmmss").format(date);
		return appId + SPLIT + dateString + SPLIT + timeString + SPLIT + size + SPLIT + uuid + SPLIT + ext;
	}

	public static String getUuidByRid(String rid) {
		validateRid(rid);
		String[] splits = rid.split(SPLIT);
		return splits[splits.length - 2];
	}
	
	public static long getSizeByRid(String rid) {
		validateRid(rid);
		String[] splits = rid.split(SPLIT);
		String sizeString = splits[3];
		if (StringUtils.isBlank(sizeString) || !sizeString.matches("\\d+")) {
			return 0;
		} else {
			return Long.valueOf(sizeString);
		}
	}

	/**
	 * 根据rid获取绝对路径，e.g. /appName/yyyy/MMdd/HHmmss_(size)_(uuid).doc
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
		return splits[splits.length - 4] + SPLIT + splits[splits.length - 3] + SPLIT + splits[splits.length - 2] + "." + splits[splits.length - 1];
	}
	
	public static void validateRid(String rid) throws IllegalArgumentException {
		if (StringUtils.isBlank(rid) || !rid.matches("\\w{1,}_\\d{8}_\\d{6}.*")) {
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
		dir = dir.replaceAll("\\\\", "/");
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

	/**
	 * 获取PDF解析文档的本地目录(PNG)
	 * 
	 * @param rid
	 * @return
	 */
	public String getParseDirOfPdf2Png(String rid) {
		String parseDir = getParseDir(rid);
		String pdf2ImgDir = parseDir + ViewService.PDF_TO_IMAGE_TYPE + File.separator;
		if (!new File(pdf2ImgDir).isDirectory()) {
			new File(pdf2ImgDir).mkdirs();
		}
		return pdf2ImgDir;
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
		String name = getFileNameByRid(rid);
		if (StringUtils.isNotBlank(name) && name.contains(".")) {
			name = name.substring(0, name.lastIndexOf("."));
		}
		return name;
	}
}