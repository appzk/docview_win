package com.idocv.docview.convert;

import com.idocv.docview.util.CmdUtil;

public class Img2Jpg {
	public static void main(String[] args) {
		// e:\test\convert.exe -resize 100x100 test.jpg testresult.jpg

		// String CMD_IMG2JPG = "C:/Program Files/ImageMagick-6.9.0-Q8/convert.exe";
		String CMD_IMG2JPG = "e:/idocv/converter/img2jpg.exe";
		String src = "e:/test/test.jpg";
		String dest = "e:/test/result.jpg";
		String result = CmdUtil.runWindows(CMD_IMG2JPG, "-resize", "100x", src, dest);
		System.out.println("Done:\n" + result);
	}
}