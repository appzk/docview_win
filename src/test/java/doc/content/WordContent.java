package doc.content;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class WordContent {
	public static void main(String[] args) {
		try {
			String lineDilimeter = "``";
			String src = "/Users/Godwin/docview/data/10/04/16/100416b93d34d3482c47a7f06ca50f29/2012/1023/fFs/fFs.html";

			String contentWhole = FileUtils.readFileToString(new File(src));
			contentWhole = contentWhole.replaceAll("\n", lineDilimeter);
			String title = contentWhole.replaceFirst("(?i).*?<TITLE>(.*?)</TITLE>.*(?i)", "$1").replaceAll(lineDilimeter, "\n");
			String body = contentWhole.replaceFirst("(?i).*?(<BODY[^>]*>)(.*?)</BODY>.*", "$2").replaceAll(lineDilimeter, "\n");
			
			List<String> pages = new ArrayList<String>();
			String pagingString = contentWhole.replaceFirst("(?i).*?(<BODY[^>]*>)(.*?)</BODY>.*", "$2");
			while (pagingString.matches(".+<[^>]+page-break-before[^>]+>.*")) {
				String page = pagingString.replaceFirst("(.+?)(<[^>]+page-break-before[^>]+>.*)", "$1").replaceAll(lineDilimeter, "\n");
				pagingString = pagingString.replaceFirst("(.+?)(<[^>]+page-break-before[^>]+>.*)", "$2");
				pages.add(page);
			}
			pages.add(pagingString.replaceAll(lineDilimeter, "\n"));

			System.out.println("Page count: " + pages.size());

			// System.out.println(body);
			
			// <P CLASS="western" ALIGN=LEFT STYLE="margin-bottom: 0in; page-break-before: always">
			// <P CLASS="western" ALIGN=CENTER STYLE="margin-bottom: 0in; page-break-before: always">
			// <H1 CLASS="western" STYLE="page-break-before: always">
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
