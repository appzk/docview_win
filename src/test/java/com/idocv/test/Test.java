package com.idocv.test;

import com.idocv.docview.util.CmdUtil;

public class Test {
	public static void main(String[] args) {
		String pdf2img = "E:/test/pdf2img/gswin64c.exe";
		String destDir = "E:/test/";
		String src = "E:/test/price.pdf";
		String result = convertPdf2Img(pdf2img, destDir, src);
		System.out.println("result: " + result);
	}
	
	public static String convertPdf2Img(String pdf2img, String destDir, String src) {
		return CmdUtil.runWindows(pdf2img, "-q", "-dSAFER", "-dBATCH",
				"-dNOPAUSE", "-r60", "-sDEVICE=png16m", "-dTextAlphaBits=4",
				"-dGraphicsAlphaBits=4", "-sOutputFile=" + destDir + "%d.png",
				src);
	}
}