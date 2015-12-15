package com.idocv.docview.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.idocv.docview.util.CmdUtil;

/**
 * 
 * ref: http://www.imagemagick.org/Usage/annotating/#wmark_image
 * 
 * @author godwin
 *
 */
public class WatermarkDemo {
	public static void main(String[] args) {
		String cmdConvert = "C:/Program Files/ImageMagick-6.9.0-Q8/convert.exe";
		// convert -composite -gravity southeast -compose overlay -geometry +20+20 sky.jpg logo.png dest.jpg
		String src = "D:/test/sky.jpg";
		String logo = "d:/test/logo.png";
		String dest = "d:/test/dest.jpg";

		String params = "-composite -gravity southeast -compose overlay -geometry +20+20";
		List<String> paramList = new ArrayList<String>();
		paramList.add(cmdConvert);
		paramList.addAll(Arrays.asList(params.split(" ")));
		paramList.add(src);
		paramList.add(logo);
		paramList.add(dest);

		String result = CmdUtil.runWindows(paramList.toArray(new String[0]));
		System.out.println("done!");
		System.out.println(result);
	}
}