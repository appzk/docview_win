package com.idocv.test;

import com.idocv.docview.util.CmdUtil;

public class Test {
	public static void main(String[] args) {
		String pdf2img = "e:/idocv/converter/pdf2img/gswin64c.exe";
		String src = "e:/test/pdf/price.pdf";
		String destDir = "e:/test/pdf/a3/";
		// dwg2bmp.bat -f -a -b white -o d:\cad\c.jpg d:\cad\test.dwg
		long start = System.currentTimeMillis();
		// String result = CmdUtil.runWindows(pdf2img, "-q", "-dNOPAUSE", "-dBATCH", "-sDEVICE=png16m", "-sPAPERSIZE=a3", "-dPDFFitPage", "-dUseCropBox", "-sOutputFile=" + destDir + "%d.png", src);
		// String result = CmdUtil.runWindows(pdf2img, "-q", "-dNOPAUSE", "-dBATCH", "-sDEVICE=png16m", "-sPAPERSIZE=a3", "-r100", "-dPDFFitPage", "-dUseCropBox", "-sOutputFile=" + destDir + "%d.png", src);
		String result = CmdUtil.runWindows(pdf2img, "-q", "-dSAFER", "-dBATCH", "-dNOPAUSE", "-r150", "-sDEVICE=png16m", "-dTextAlphaBits=4", "-dGraphicsAlphaBits=4", "-sOutputFile=" + destDir + "%d.png", src);
		
		// gs -dSAFER -dBATCH -dNOPAUSE -r150 -sDEVICE=pnggray -dTextAlphaBits=4 -sOutputFile=doc-%02d.png doc.pdf
		long end = System.currentTimeMillis();
		System.out.println("elapse: " + (end - start));
		System.out.println("result: " + result);
	}
}