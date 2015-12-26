package com.idocv.docview.convert;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import com.idocv.docview.util.CmdUtil;

public class ImageCompress {
	private static String CMD_GM = "c:/Program Files/GraphicsMagick-1.3.20-Q8/gm";
	private static String CMD_FFMPEG = "d:/ffmpeg/bin/ffmpeg";
	private static Set<String> EXT_SET = new HashSet<String>();

	private static String CONVERT_TYPE = "jpg"; // jpg or mov

	static {
		EXT_SET.add("jpg");
		EXT_SET.add("mov");
	}
	
	private static String INIT_DIR = "d:/test";

	public static void main(String[] args) throws Exception {
		File srcDir = new File(INIT_DIR);
		File destDir = new File(srcDir, "tiny" + CONVERT_TYPE);
		if (!destDir.isDirectory()) {
			destDir.mkdirs();
		}

		int allLength = srcDir.listFiles().length;

		File[] imgs = srcDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (null != name && name.contains(".")) {
					String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
					if (EXT_SET.contains(ext)) {
						return true;
					}
				}
				return false;
			}
		});

		log("There are " + imgs.length + "/" + allLength + " JPG or MOV files.");
		Thread.sleep(2000);

		StringBuffer sb = new StringBuffer();
		int count = 0;
		for (File img : imgs) {
			count++;
			String src = img.getAbsolutePath();
			String origName = img.getName();
			if (!origName.contains(".")) {
				continue;
			}
			int dotIndex = origName.lastIndexOf(".");
			String nameWithoutExt = origName.substring(0, dotIndex);
			if (CONVERT_TYPE.equalsIgnoreCase("jpg") && src.toLowerCase().endsWith("." + CONVERT_TYPE)) {
				String dest = destDir.getAbsolutePath() + File.separator + img.getName();
				CmdUtil.runWindows(CMD_GM, "convert", src, dest);
			} else if (CONVERT_TYPE.equalsIgnoreCase("mov") && src.toLowerCase().endsWith("." + CONVERT_TYPE)) {
				String dest = destDir.getAbsolutePath() + File.separator + nameWithoutExt + ".mp4";
				CmdUtil.runWindows(CMD_FFMPEG, "-i", src, dest);
			} else {
				continue;
			}
			log("convert " + img.getName() + "(" + count + "/" + imgs.length + ") done!");
			Thread.sleep(500);
		}
		System.out.println(sb);
		System.out.println("--------------------------------");
		System.err.println("All done!");
		// gm convert -resize 200x200 t.jpg tt.jpg
	}
	
	public static void log(String msg) {
		System.out.println(msg);
	}
}
