package com.idocv.docview.convert;

import com.idocv.docview.util.CmdUtil;

public class Dwg2Jpg {
	public static void main(String[] args) {
		String dwg2jpg = "D:/qcad-3.9.4-pro-win/qcad.exe -no-gui -autostart scripts/Pro/Tools/Dwg2Bmp/Dwg2Bmp.js";
		String src = "D:/cad/test.dwg";
		String destPath = "D:/cad/test_r.jpg";
		// dwg2bmp.bat -f -a -b white -o d:\cad\c.jpg d:\cad\test.dwg
		long start = System.currentTimeMillis();
		String result = CmdUtil.runWindows(dwg2jpg, "-f", "-a", "-b", "white", "-r", "1/1", "-o", destPath, src);
		long end = System.currentTimeMillis();
		System.out.println("elapse: " + (end - start));
		System.out.println("result: " + result);
	}
}