package com.idocv.docview.content;

public class WordPicture {
	public static void main(String[] args) {
		// String src = "/Users/Godwin/tmp/docview/windows/word/index.html";
		// String contentWhole = FileUtils.readFileToString(new File(src), "GBK");
		String contentWhole = "<p class=MsoNormal align=center style='text-align:center'><img width=353 height=114 src=\"index.files/image002.png\" align=left></p>";
		System.out.println(contentWhole);
		System.err.println("========================================");
		System.out.println(processPictureUrl("RID", contentWhole));
	}
	
	public static String processPictureUrl(String rid, String content) {
		return content.replaceAll("(?s)(?i)(<img[^>]+?src=\")([^>]+?>)(?-i)", "$1" + "<URL DIR>" + "$2");
	}
}