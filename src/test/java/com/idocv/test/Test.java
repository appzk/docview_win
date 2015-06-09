package com.idocv.test;

import com.idocv.docview.util.CmdUtil;

public class Test {
	public static void main(String[] args) {
		String cmd = "D:/zip/7z.exe";
		String zipSrc = "D:/zip/test.rar";
		String destDir = "D:/zip/ttt";
		String result = CmdUtil.runWindows(cmd, "e", zipSrc, "-o" + destDir, "-r", "-y");
		System.out.println("result: " + result);
	}
}