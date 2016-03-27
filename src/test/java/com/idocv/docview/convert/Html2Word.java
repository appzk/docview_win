package com.idocv.docview.convert;

import com.idocv.docview.util.CmdUtil;

public class Html2Word {

	private static String HTML2WORD = "e:/test/pandoc.exe";

	public static void main(String[] args) {
		String src = "e:/test/body.html";
		String dest = "e:/test/test.docx";
		String result = CmdUtil.runWindows("cd /D", "e:/test", "&", HTML2WORD, src, "-o", dest);
		System.out.println("result: " + result);
	}

}