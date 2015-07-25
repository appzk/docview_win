package com.idocv.docview.convert;

import com.idocv.docview.util.CmdUtil;

public class Img2Jpg {
	public static void main(String[] args) {
		String CMD_IMG2JPG = "D:/idocv/converter/img2jpg.exe";
		String dest = "D:/test/background.gif";
		String src = "D:/test/background.jpg";
		String result = CmdUtil.runWindows(CMD_IMG2JPG, "-resize", "1000x", src, dest);
		System.out.println("Done:\n" + result);
	}
}