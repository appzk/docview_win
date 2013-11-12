package com.idocv.docview.convert;

import com.idocv.docview.util.CmdUtil;

public class Pdf2Png {
	public static void main(String[] args) {
		try {
			String src = "d:/test/pdfbox/test.pdf";
			String destDir = "d:/test/pdfbox/png"; // Directory MUST exist
			String pdf2image = "D:/test/pdfbox/pdf2img.jar";
			String result = CmdUtil.runWindows("java", "-jar", pdf2image, "PDFToImage", "-imageType", "png", "-outputPrefix", destDir, src);
			System.out.println("result: \n" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}