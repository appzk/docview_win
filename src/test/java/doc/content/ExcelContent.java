package doc.content;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.idocv.docview.vo.ExcelVo;


public class ExcelContent {
	public static void main(String[] args) {
		try {
			String lineDilimeter = "``";
			String src = "/Users/Godwin/docview/data/10/04/16/100416b93d34d3482c47a7f06ca50f29/2012/1017/mn5/mn5.html";
			String contentWhole = FileUtils.readFileToString(new File(src));
			contentWhole = contentWhole.replaceAll("\n", lineDilimeter);
			System.out.println("contentWhole:\n" + contentWhole);

			String titlesString = contentWhole.replaceFirst("(?i).*?<HR>(.*?)<HR>.*", "$1");
			List<String> titles = new ArrayList<String>();
			while (titlesString.contains("</A>")) {
				String title = titlesString.replaceFirst(".*?<A HREF=\"#table[^\"]+\">([^<]+)</A>.*", "$1");
				titles.add(title);
				titlesString = titlesString.substring(titlesString.indexOf("</A>") + 4);
			}
			System.out.println("titles:\n" + titles);

			String content = contentWhole;
			List<ExcelVo> tables = new ArrayList<ExcelVo>();
			for (String title : titles) {
				ExcelVo vo = new ExcelVo();
				String c = content.replaceFirst("(?i).*?(<TABLE[^>]+>.*?</TABLE>).*(?-i)", "$1").replaceAll(lineDilimeter, "\n");
				// FileUtils.writeStringToFile(file, c);
				String url = "";
				content = content.substring(content.indexOf("</TABLE>") + 8);
				vo.setTitle(title);
				vo.setUrl(url);
				tables.add(vo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
