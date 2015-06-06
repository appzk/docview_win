package com.idocv.test;

import com.idocv.docview.util.CmdUtil;

public class Test {
	public static void main(String[] args) {
		String pdf2html = "D:/pdf2htmlEX/pdf2htmlEX.exe";
		String src = "D:/pdf2htmlEX/test.pdf";
		String destDir = "D:\\pdf2htmlEX\\ttt";
		String result = CmdUtil.runWindows(pdf2html, "--embed", "cfijo",
				"--fallback", "1", "--dest-dir", destDir, src, "index.html");
		System.out.println("result:\n" + result);

	}
}