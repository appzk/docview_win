package com.idocv.docview.common;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * Doc View File Types
 * 
 * @author godwin668@gmail.com
 * @version 1.0 2015-02-02
 * 
 */
public enum ViewType {
	
	/**
	 * Word
	 */
	WORD("w"),

	/**
	 * Excel
	 */
	EXCEL("x"),

	/**
	 * PPT
	 */
	PPT("p"),

	/**
	 * PDF
	 */
	PDF("f"),

	/**
	 * TXT
	 */
	TXT("t"),

	/**
	 * Image
	 */
	IMG("i"),

	/**
	 * Audio
	 */
	AUDIO("a"),

	/**
	 * Video
	 */
	VIDEO("v"),

	/**
	 * Other
	 */
	OTHER("o");

	private String symbol;
	private Set<String> extSet = new HashSet<String>();
	
	static {
		WORD.extSet.add("doc");
		WORD.extSet.add("docx");

		EXCEL.extSet.add("xls");
		EXCEL.extSet.add("xlsx");

		PPT.extSet.add("ppt");
		PPT.extSet.add("pptx");

		PDF.extSet.add("pdf");

		TXT.extSet.add("txt");

		IMG.extSet.add("jpg");
		IMG.extSet.add("jpeg");
		IMG.extSet.add("png");
		IMG.extSet.add("bmp");
		IMG.extSet.add("gif");
		IMG.extSet.add("tif");

		AUDIO.extSet.add("mp3");
		AUDIO.extSet.add("midi");
		AUDIO.extSet.add("wma");

		VIDEO.extSet.add("avi");
		VIDEO.extSet.add("rm");
		VIDEO.extSet.add("rmvb");
		VIDEO.extSet.add("mpeg");
		VIDEO.extSet.add("dat");
		VIDEO.extSet.add("mov");
	}

	private ViewType(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return this.symbol;
	}

	/**
	 * Get ViewType by symbol: w, x, p, f, t, i, a, v, o
	 * 
	 * @param symbol
	 * @return
	 */
	public static ViewType getViewTypeBySymbol(String symbol) {
		if (StringUtils.isBlank(symbol)) {
			return ViewType.OTHER;
		}
		symbol = symbol.toLowerCase();
		if (ViewType.WORD.extSet.contains(symbol)) {
			return ViewType.WORD;
		} else if (ViewType.EXCEL.symbol.equals(symbol)) {
			return ViewType.EXCEL;
		} else if (ViewType.PPT.symbol.equals(symbol)) {
			return ViewType.PPT;
		} else if (ViewType.PDF.symbol.equals(symbol)) {
			return ViewType.PDF;
		} else if (ViewType.TXT.symbol.equals(symbol)) {
			return ViewType.TXT;
		} else if (ViewType.IMG.symbol.equals(symbol)) {
			return ViewType.IMG;
		} else if (ViewType.AUDIO.symbol.equals(symbol)) {
			return ViewType.AUDIO;
		} else if (ViewType.VIDEO.symbol.equals(symbol)) {
			return ViewType.VIDEO;
		} else {
			return ViewType.OTHER;
		}
	}

	public static ViewType getViewTypeByExt(String ext) {
		if (StringUtils.isBlank(ext)) {
			return ViewType.OTHER;
		}
		ext = ext.toLowerCase();
		if (ViewType.WORD.extSet.contains(ext)) {
			return ViewType.WORD;
		} else if (ViewType.EXCEL.extSet.contains(ext)) {
			return ViewType.EXCEL;
		} else if (ViewType.PPT.extSet.contains(ext)) {
			return ViewType.PPT;
		} else if (ViewType.PDF.extSet.contains(ext)) {
			return ViewType.PDF;
		} else if (ViewType.TXT.extSet.contains(ext)) {
			return ViewType.TXT;
		} else if (ViewType.IMG.extSet.contains(ext)) {
			return ViewType.IMG;
		} else if (ViewType.AUDIO.extSet.contains(ext)) {
			return ViewType.AUDIO;
		} else if (ViewType.VIDEO.extSet.contains(ext)) {
			return ViewType.VIDEO;
		} else {
			return ViewType.OTHER;
		}
	}

	public static void main(String[] args) {
		String ext = "jpeg";
		System.out.println("ext symbol: " + ViewType.getViewTypeByExt(ext));
		System.out.println(ViewType.WORD == ViewType.getViewTypeByExt("pdf"));

		System.out.println(ViewType.getViewTypeBySymbol("a"));
	}
}