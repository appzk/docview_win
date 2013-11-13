package com.idocv.docview.convert;

import com.idocv.docview.util.CmdUtil;

public class Pdf2Png {
	public static void main(String[] args) {
		try {
			String src = "d:/test/pdfbox/test.pdf";
			String destDir = "d:/test/pdfbox/png"; // Directory MUST exist
			String converter = "D:/test/pdfbox/pdf2img.jar";
			// String result = pdf2PngByApachePdfBox(converter, src, destDir);
			// System.out.println("result: \n" + result);
			
			converter = "c:/Program Files/gs/gs9.10/bin/gswin64c.exe";
			src = "d:/test/ghostscript/lm.pdf";
			destDir = "d:/test/ghostscript/";
			String result = pdf2PngByGhostscript(converter, src, destDir);
			System.out.println("Ghostscript convert:\n" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String pdf2PngByGhostscript(String converter, String src, String destDir) {
		try {
			String result = CmdUtil.runWindows(converter, "-q", "-dNOPAUSE", "-dBATCH", "-sDEVICE=png16m", "-sPAPERSIZE=a2", "-dPDFFitPage", "-dUseCropBox", "-sOutputFile=" + destDir + "%d.png", src);
			System.out.println("result: \n" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "error: " + e.getMessage();
		}
	}
	
	public static String pdf2PngByApachePdfBox(String converter, String src, String destDir) {
		try {
			String result = CmdUtil.runWindows("java", "-jar", converter, "PDFToImage", "-imageType", "png", "-outputPrefix", destDir, src);
			System.out.println("result: \n" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "error: " + e.getMessage();
		}
	}
}