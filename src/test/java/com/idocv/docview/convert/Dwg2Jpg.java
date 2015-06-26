package com.idocv.docview.convert;

import com.idocv.docview.util.CmdUtil;

public class Dwg2Jpg {
	
	private static String CMD_DWG2JPG_QCAD = "D:/qcad-3.9.4-pro-win/qcad.exe -no-gui -autostart scripts/Pro/Tools/Dwg2Bmp/Dwg2Bmp.js";
	private static String CMD_DWG2JPG_TOTALCAD = "C:/Program Files (x86)/TotalCADConverter/CADConverter.exe";
	private static String CMD_DWG2JPG_ANYDWG = "e:/idocv/converter/cad2img/di.exe";
	
	public static void main(String[] args) {
		String src = "e:/test/cad/Architecture.dwg";
		String destPath = "e:/test/cad/test_any.png";
		
		// convert by QCAD
		// convertDwg2ImgByQcad(src, destPath);
		
		// convert by TotalCADConverter
		// convertDwg2ImgByTotalCADConverter(src, destPath);
		
		// Convert by anydwg
		convertDwg2ImgByAnyDwg(src, destPath);
	}
	
	/**
	 * Convert dwg by QCad (do NOT support 3D dwg)
	 * 
	 * @param src
	 * @param destPath
	 */
	@Deprecated
	public static void convertDwg2ImgByQcad(String src, String destPath) {
		// dwg2bmp.bat -f -a -b white -o d:\cad\c.jpg d:\cad\test.dwg
		long start = System.currentTimeMillis();
		String result = CmdUtil.runWindows(CMD_DWG2JPG_QCAD, "-f", "-a", "-b", "white", "-r", "1/1", "-o", destPath, src);
		long end = System.currentTimeMillis();
		System.out.println("elapse: " + (end - start));
		System.out.println("result: " + result);
	}
	
	/**
	 * Convert dwg by TotalCAD (a little bit expensive, $350 for a Server License)
	 * 
	 * @param src
	 * @param destPath
	 */
	@Deprecated
	public static void convertDwg2ImgByTotalCADConverter(String src, String destPath) {
		long start = System.currentTimeMillis();
		String result = CmdUtil.runWindows(CMD_DWG2JPG_TOTALCAD, src, destPath, "-c", "jpg", "-s", "500");
		long end = System.currentTimeMillis();
		System.out.println("elapse: " + (end - start));
		System.out.println("result: " + result);
	}
	
	/**
	 * Convert dwg by http://anydwg.com/dwg2img
	 * 
	 * @param src
	 * @param destPath
	 */
	public static void convertDwg2ImgByAnyDwg(String src, String destPath) {
		long start = System.currentTimeMillis();
		// di.exe /DPI 96 /Overwrite /OutLayout All /Hide /InFile D:\sample.dwg /OutFile D:\sample.tif
		
		src = src.replaceAll("/", "\\\\");
		destPath = destPath.replaceAll("/", "\\\\");
		
		String result = CmdUtil.runWindows(CMD_DWG2JPG_ANYDWG, "/Overwrite", "/OutLayout", "All", "/Hide", "/InFile", src, "/OutFile", destPath);
		long end = System.currentTimeMillis();
		System.out.println("elapse: " + (end - start));
		System.out.println("result: " + result);
	}
}