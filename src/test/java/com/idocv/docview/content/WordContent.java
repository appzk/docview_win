package com.idocv.docview.content;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class WordContent {
	public static void main(String[] args) {
		try {
			String src = "/Users/Godwin/tmp/docview/windows/word/index.html";
			File dest = new File("/Users/Godwin/tmp/docview/windows/word/bo.html");

			String contentWhole = FileUtils.readFileToString(new File(src), "GBK");
			String body = contentWhole.replaceFirst("(?s)(?i).*?(<BODY[^>]*>)(.*?)</BODY>.*", "$2");
			FileUtils.writeStringToFile(dest, body, "utf-8");
			
			List<String> pages = new ArrayList<String>();
			String pagingString = contentWhole.replaceFirst("(?s)(?i).*?(<BODY[^>]*>)(.*?)</BODY>.*", "$2");
			while (pagingString.matches("(?s).+<[^>]+page-break-before[^>]+>.*")) {
				String page = pagingString.replaceFirst("(?s)(.+?)(<[^>]+page-break-before[^>]+>.*)", "$1");
				pagingString = pagingString.replaceFirst("(?s)(.+?)(<[^>]+page-break-before[^>]+>.*)", "$2");
				pages.add(page);
			}
			pages.add(pagingString);

			System.out.println("Page count: " + pages.size());
			for (int i = 0; i < pages.size(); i++) {
				System.err.println("===>>> page " + (i + 1));
				System.out.println(pages.get(i));
			}

			// System.out.println(body);
			
			// <P CLASS="western" ALIGN=LEFT STYLE="margin-bottom: 0in; page-break-before: always">
			// <P CLASS="western" ALIGN=CENTER STYLE="margin-bottom: 0in; page-break-before: always">
			// <H1 CLASS="western" STYLE="page-break-before: always">
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
