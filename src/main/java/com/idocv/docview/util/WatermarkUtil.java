package com.idocv.docview.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class WatermarkUtil {

	private static Logger logger = LoggerFactory.getLogger(WatermarkUtil.class);

	// private static final String CONVERT_CMD_PARAMS_DEFAULT = "-composite -gravity southeast -geometry +10+10";
	private static final String CONVERT_CMD_PARAMS_DEFAULT = "<logo> miff:- | composite -dissolve 50 -gravity southeast -geometry +10+10 - <src> <dest>";

	public static String watermarkImg(String convertCmd, String convertCmdParams, String src, String logo, String dest) {
		if (StringUtils.isBlank(convertCmdParams)) {
			convertCmdParams = CONVERT_CMD_PARAMS_DEFAULT;
		}
		src = src.replaceAll("\\\\", "/");
		logo = logo.replaceAll("\\\\", "/");
		dest = dest.replaceAll("\\\\", "/");

		convertCmdParams = convertCmdParams.replaceFirst("<src>", src);
		convertCmdParams = convertCmdParams.replaceFirst("<logo>", logo);
		convertCmdParams = convertCmdParams.replaceFirst("<dest>", dest);

		List<String> paramList = new ArrayList<String>();
		paramList.add(convertCmd);
		paramList.addAll(Arrays.asList(convertCmdParams.split(" ")));
		paramList.add(src);
		paramList.add(logo);
		paramList.add(dest);

		String result = CmdUtil.runWindows(paramList.toArray(new String[0]));
		logger.debug("[WATERMARK IMG] " + result);
		return result;
	}

	public static String watermarkDir(String convertCmd, String convertCmdParams, File dir, String logo) {
		String result = "";
		Collection<File> watermarkImgFiles = FileUtils.listFiles(dir, new String[] { "jpg", "png" }, true);
		if (!CollectionUtils.isEmpty(watermarkImgFiles)) {
			for (File watermarkImgFile : watermarkImgFiles) {
				String imgResult = WatermarkUtil.watermarkImg(convertCmd, convertCmdParams, watermarkImgFile.getAbsolutePath(), logo, watermarkImgFile.getAbsolutePath());
				result += imgResult;
			}
		}
		logger.info("[WATERMARK DIR] " + result);
		return result;
	}

	public static void main(String[] args) {
		String convertCmd = "C:/Program Files/ImageMagick-6.9.0-Q8/convert.exe";
		File srcDir = new File("E:/test/test");
		String logo = "e:/test/logo.png";
		String params = "<logo> miff:- | composite -dissolve 50 -gravity southeast -geometry +10+10 - <src> <dest>";
		String result = watermarkDir(convertCmd, params, srcDir, logo);
		System.err.println("Done!");
		System.out.println(result);
	}
}
