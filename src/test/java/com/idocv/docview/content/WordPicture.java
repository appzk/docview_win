package com.idocv.docview.content;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


public class WordPicture {
	public static void main(String[] args) {
		try {
			String src = "/Users/Godwin/tmp/docview/windows/word/index.html";
			String contentWhole = FileUtils.readFileToString(new File(src), "GBK");
			System.out.println(contentWhole);
			System.err.println("========================================");
			System.out.println(processPictureUrl("RID", contentWhole));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String processPictureUrl(String rid, String content) {
		return content.replaceAll("(?s)(?i)(<img.*?src=\")([^>]+?>)(?-i)", "$1" + "<URL DIR>" + "$2");
	}
}