package com.idocv.docview.content;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.vo.ExcelVo;


public class ExcelContent {
	public static void main(String[] args) {
		try {
			File srcDir = new File("/Users/Godwin/tmp/docview/windows/example_excel2html/index.files");
			if (!srcDir.isDirectory()) {
				System.err.println("Directory " + srcDir.getAbsolutePath() + " NOT found!");
				System.exit(0);
			}
			File[] excelFiles = srcDir.listFiles();
			List<File> sheetFiles = new ArrayList<File>();
			File tabstripFile = null;
			for (File excelFile : excelFiles) {
				if (excelFile.getName().matches("sheet\\d+\\.html")) {
					sheetFiles.add(excelFile);
				} else if (excelFile.getName().equalsIgnoreCase("tabstrip.html")) {
					tabstripFile = excelFile;
				}
			}
			if (CollectionUtils.isEmpty(sheetFiles) || null == tabstripFile) {
				throw new Exception("Excel parsed files NOT found!");
			}
			
			// get TITLE(s) and CONTENT(s)
			List<ExcelVo> VoList = new ArrayList<ExcelVo>();
			// List<String> titleList = new ArrayList<String>();
			// List<String> contentList = new ArrayList<String>();
			String titleFileContent = FileUtils.readFileToString(tabstripFile, "GBK");
			for (int i = 0; i < sheetFiles.size(); i++) {
				ExcelVo vo = new ExcelVo();

				// get title
				String title = titleFileContent.replaceFirst("(?s)(?i).+?" + sheetFiles.get(i).getName() + ".+?<font[^>]+>(.+?)</font>.*(?-i)", "$1");
				title = StringUtils.isBlank(title) ? ("表单" + (i + 1)) : title;
				// titleList.add(title);
				System.err.println("title" + (i + 1) + " = " + title);

				// get content
				String sheetFileContent = FileUtils.readFileToString(sheetFiles.get(i), "GBK");
				String sheetContent = sheetFileContent.replaceFirst("(?s)(?i).+?<body.+?(<table[^>]+>.*?</table>).*(?-i)", "$1");
				// sheetContent = processPictureUrl(rid, sheetContent);
				// contentList.add(sheetContent);

				vo.setTitle(title);
				vo.setContent(sheetContent);
				VoList.add(vo);
			}

			//PageVo<ExcelVo> page = new PageVo<ExcelVo>(tables, titles.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
