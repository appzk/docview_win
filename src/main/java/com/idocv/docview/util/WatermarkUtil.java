package com.idocv.docview.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class WatermarkUtil {

	private static Logger logger = LoggerFactory.getLogger(WatermarkUtil.class);

	public static String watermarkImg(String convertCmd, String src, String logo, String dest) {
		String params = "-composite -gravity southeast -geometry +10+10";
		List<String> paramList = new ArrayList<String>();
		paramList.add(convertCmd);
		paramList.addAll(Arrays.asList(params.split(" ")));
		paramList.add(src);
		paramList.add(logo);
		paramList.add(dest);

		String result = CmdUtil.runWindows(paramList.toArray(new String[0]));
		logger.info("[WATERMARK IMG] " + result);
		return result;
	}

	public static String watermarkDir(String convertCmd, File dir, String logo) {
		String result = "";
		Collection<File> watermarkImgFiles = FileUtils.listFiles(dir, new String[] { "jpg", "png" }, true);
		if (!CollectionUtils.isEmpty(watermarkImgFiles)) {
			for (File watermarkImgFile : watermarkImgFiles) {
				String imgResult = WatermarkUtil.watermarkImg(convertCmd, watermarkImgFile.getAbsolutePath(), logo, watermarkImgFile.getAbsolutePath());
				result += imgResult;
			}
		}
		logger.info("[WATERMARK DIR] " + result);
		return result;
	}

	public static void main(String[] args) {
		String convertCmd = "C:/Program Files/ImageMagick-6.9.0-Q8/convert.exe";
		File srcDir = new File("E:/test/232541_380397_isUIAQp");
		String logo = "e:/test/logo.png";
		String result = watermarkDir(convertCmd, srcDir, logo);
		System.err.println("Done!");
		System.out.println(result);
	}
}
