package com.idocv.docview.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageConverter {

	public static void main(String[] args) throws Exception {
		File srcDir = new File("d:/test");
		File destDir = new File(srcDir, "tiny");
		if (!destDir.isDirectory()) {
			destDir.mkdirs();
		}
		File[] imgs = srcDir.listFiles();
		StringBuffer sb = new StringBuffer();
		int count = 0;
		for (File img : imgs) {
			count++;
			String src = img.getAbsolutePath();
			if (!src.toLowerCase().endsWith(".jpg")) {
				continue;
			}
			String dest = destDir.getAbsolutePath() + File.separator + img.getName();
			runWindows("C:/Program Files/ImageMagick-6.9.0-Q8/convert.exe", src, dest);
			log("convert " + img.getName() + "(" + count + "/" + imgs.length + ") done!");
			Thread.sleep(500);
		}
		System.out.println(sb);
		System.out.println("--------------------------------");
		System.err.println("All done!");
		// gm convert -resize 200x200 t.jpg tt.jpg
	}

	public static String runWindows(String... cmd) {
		String result = "";
		StringBuffer sbOut = new StringBuffer();
		StringBuffer sbErr = new StringBuffer();
		try {
			String line;
			String[] use = { "cmd", "/c" };
			List<String> cmdList = new ArrayList<String>(Arrays.asList(use));
			cmdList.addAll(Arrays.asList(cmd));

			ProcessBuilder builder = new ProcessBuilder(
					cmdList.toArray(new String[0]));
			builder.redirectErrorStream(true);
			long start = System.currentTimeMillis();
			Process p = builder.start();

			BufferedReader bri = new BufferedReader(new InputStreamReader(
					p.getInputStream(), "GBK"));
			while ((line = bri.readLine()) != null) {
				sbOut.append(line + "\n");
			}
			bri.close();
			Integer exitValue = p.waitFor();
			long end = System.currentTimeMillis();
			if (0 == exitValue || 1 == exitValue) {
				// do something here...
			}
			log("[CMD] run " + cmdList + " success within " + (end - start) + " milisecond(s).");
			result = exitValue + ":" + sbOut.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void log(String msg) {
		System.out.println(msg);
	}
}