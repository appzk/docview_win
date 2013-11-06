package com.idocv.docview.convert;

import com.idocv.docview.util.CmdUtil;


public class Pdf2Html {

	private// @Value("${pdf.cmd.pdf2html}")
	static String pdf2html;

	public static void main(String[] args) {
		pdf2html = "D:/idocv/bin/converter/pdf2html/pdf2htmlEX.exe";
		pdf2html = "D:/test/pdf2html/pdf2htmlEX.exe";
		String src = "D:/test/test.pdf";
		String destDir = "d:/test/test";
		String result = CmdUtil.runWindows(pdf2html, "--dest-dir", destDir.replaceAll("/", "\\\\"), "--embed", "cfijo", src, "index.html");
		System.out.println(result);
	}
}
