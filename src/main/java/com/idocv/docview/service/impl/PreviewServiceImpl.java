package com.idocv.docview.service.impl;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.idocv.docview.common.DocServiceException;
import com.idocv.docview.service.PreviewService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.ExcelVo;
import com.idocv.docview.vo.PPTVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.TxtVo;
import com.idocv.docview.vo.WordVo;

@Service
public class PreviewServiceImpl implements PreviewService, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(PreviewServiceImpl.class);
	
	@Resource
	private RcUtil rcUtil;
	
	private static String lineDilimeter = "``";
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO
	}

	@Override
	public PageVo<WordVo> convertWord2Html(String rid, int start, int limit) throws DocServiceException{
		try {
			convert(rid);
			File htmlFile = new File(rcUtil.getParsePathOfHtml(rid));
			
			// read body
			File bodyPath = new File(rcUtil.getParseDir(rid) + "body.html");
			String bodyRaw;
			if (!bodyPath.isFile()) {
				String contentWhole = FileUtils.readFileToString(htmlFile);
				contentWhole = contentWhole.replaceAll("\n|\r", lineDilimeter);
				String title = contentWhole.replaceFirst("(?i).*?<TITLE>(.*?)</TITLE>.*(?i)", "$1").replaceAll(lineDilimeter, "\n");
				bodyRaw = contentWhole.replaceFirst("(?i).*?(<BODY[^>]*>)(.*?)</BODY>.*", "$2").replaceAll(lineDilimeter, "\n");
				FileUtils.writeStringToFile(bodyPath, bodyRaw, "UTF-8");
			} else {
				bodyRaw = FileUtils.readFileToString(bodyPath);
			}

			String bodyString = bodyRaw.replaceAll("\n|\r", lineDilimeter);

			// modify picture path from RELATIVE to ABSOLUTE url.
			bodyString = processPictureUrl(rid, bodyString);
			
			// paging
			List<String> pages = new ArrayList<String>();
			while (bodyString.matches("(?i)(.+?)(<[^>]+style=\"[^>]*page-break-before[^>]+>.*)(?-i)")) {
				String page = bodyString.replaceFirst("(?i)(.+?)(<[^>]+style=\"[^>]*page-break-before[^>]+>.*)(?-i)", "$1").replaceAll(lineDilimeter, "\n");
				bodyString = bodyString.replaceFirst("(?i)(.+?)(<[^>]+style=\"[^>]*page-break-before[^>]+>.*)(?-i)", "$2");
				pages.add(page);
			}
			pages.add(bodyString.replaceAll(lineDilimeter, "\n"));
			
			List<WordVo> data = new ArrayList<WordVo>();
			// construct vo
			for (String page : pages) {
				WordVo word = new WordVo();
				word.setContent(page);
				data.add(word);
			}
			PageVo<WordVo> page = new PageVo<WordVo>(data, 1);
			return page;
		} catch (Exception e) {
			logger.error("convertWord2Html error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<ExcelVo> convertExcel2Html(String rid, int start, int limit) throws DocServiceException {
		try {
			convert(rid);
			File src = new File(rcUtil.getPath(rid));
			
			File dest = new File(rcUtil.getParsePathOfHtml(rid));
			String contentWhole = FileUtils.readFileToString(dest);
			contentWhole = contentWhole.replaceAll("\n|\r", lineDilimeter);

			// get titles
			String titlesString = "表单-1";
			if (-1 != contentWhole.indexOf("<HR>")) {
				titlesString = contentWhole.replaceFirst("(?i).*?<HR>(.*?)<HR>.*", "$1");
			}
			List<String> titles = new ArrayList<String>();
			while (titlesString.contains("</A>")) {
				String title = titlesString.replaceFirst(".*?<A HREF=\"#table[^\"]+\">([^<]+)</A>.*", "$1");
				titles.add(title);
				titlesString = titlesString.substring(titlesString.indexOf("</A>") + 4);
			}
			if (titles.isEmpty()) {
				titles.add("表单-1");
			}
			System.out.println("titles:\n" + titles);
			
			Workbook w = null;
			try {
				w = Workbook.getWorkbook(src);
			} catch (Exception e) {
				logger.error("read excel " + src + " error:", e);
			}
			
			String content = contentWhole;
			content = processPictureUrl(rid, content);
			List<ExcelVo> tables = new ArrayList<ExcelVo>();
			for (int i = 0; i < titles.size(); i++) {
				
				// get sheet width in pixels
				int widthSum = 0;
				if (null != w) {
					Sheet sheet = w.getSheet(i);
					for (int j = 0; j < sheet.getColumns(); j++) {
						widthSum += sheet.getColumnWidth(j);
					}
				}
				widthSum = (int) (widthSum * 8.64); // convert to pixels
				String c;
				if (widthSum > 0) {
					c = content.replaceFirst("(?i).*?(<TABLE[^>]+>)(.*?</TABLE>).*(?-i)", "<TABLE border=\"1\" width=\"" + widthSum + "\">$2").replaceAll(lineDilimeter, "\n");
				} else {
					c = content.replaceFirst("(?i).*?(<TABLE[^>]+>)(.*?</TABLE>).*(?-i)", "<TABLE border=\"1\">$2").replaceAll(lineDilimeter, "\n");
				}
				content = content.substring(content.indexOf("</TABLE>") + 8);

				ExcelVo vo = new ExcelVo();
				vo.setTitle(titles.get(i));
				vo.setContent(c);
				tables.add(vo);
			}
			PageVo<ExcelVo> page = new PageVo<ExcelVo>(tables, titles.size());
			return page;
		} catch (Exception e) {
			logger.error("convertExcel2Html error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<PPTVo> convertPPT2Html(String rid, int start, int limit) throws DocServiceException {
		try {
			if (!new File(rcUtil.getParseDir(rid) + "img0.jpg").isFile()) {
				convert(rid);
			}

			// get page count
			int count = 0;
			while (new File(rcUtil.getParseDir(rid) + "img" + count + ".jpg").isFile()) {
				count++;
			}
			
			List<PPTVo> data = new ArrayList<PPTVo>();
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					PPTVo ppt = new PPTVo();
					String url = rcUtil.getParseUrlDir(rid) + "img" + i + ".jpg";
					ppt.setUrl(url);
					data.add(ppt);
				}
			}
			PageVo<PPTVo> page = new PageVo<PPTVo>(data, 1);
			return page;
		} catch (Exception e) {
			logger.error("convertPPT2Html error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<TxtVo> getTxtContent(String rid) throws DocServiceException {
		try {
			File src = new File(rcUtil.getPath(rid));
			List<TxtVo> data = new ArrayList<TxtVo>();
			String content = FileUtils.readFileToString(src, getEncoding(src));
			String[] paragraphs = content.split("\n");
			for (String para : paragraphs) {
				TxtVo vo = new TxtVo();
				String c = "<P STYLE=\"margin-bottom: 0in\"><FONT FACE=\"微软雅黑, serif\">" + para + "</FONT></P>";
				vo.setContent(c);
				data.add(vo);
			}
			PageVo<TxtVo> page = new PageVo<TxtVo>(data, 1);
			return page;
		} catch (IOException e) {
			logger.error("getTxtContent error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public String convertPdf2Swf(String rid) throws DocServiceException {
		try {
			String src = rcUtil.getPath(rid);
			String dest = rcUtil.getParseDir(rid) + RcUtil.getFileNameWithoutExt(RcUtil.getFileNameByRid(rid)) + ".swf";
			File destFile = new File(dest);
			if (!destFile.isFile()) {
				Runtime runtime = Runtime.getRuntime();
				runtime.exec("pdf2swf " + src + " -o " + dest + " -f -T 9 -t -s storeallcharacters").waitFor();
				if (!destFile.isFile()) {
					System.out.println("Can't convert \"" + src + "\" to \"" + dest + "\", start converting using poly2bitmap parameter...");
					runtime.exec("pdf2swf " + src + " -o " + dest + " -f -T 9 -t -s storeallcharacters -s poly2bitmap").waitFor();
				}
			}
			return rcUtil.getParseUrlDir(rid) + RcUtil.getFileNameWithoutExt(RcUtil.getFileNameByRid(rid)) + ".swf";
		} catch (Exception e) {
			logger.error("convertPdf2Swf error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	private boolean convert(String rid) throws DocServiceException {
		try {
			File src = new File(rcUtil.getPath(rid));
			File dest = new File(rcUtil.getParsePathOfHtml(rid));
			if (!dest.isFile()) {
				// getConverter().convert(src, dest);
			}
			return dest.isFile();
		} catch (Exception e) {
			logger.error("convert error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	/**
	 * modify picture path from RELATIVE to ABSOLUTE url.
	 * @param content
	 * @return
	 */
	public String processPictureUrl(String rid, String content) throws DocServiceException {
		return content.replaceAll("(<IMG.*?SRC=\")([^\"]+/)?([^>]*?>)", "$1" + rcUtil.getParseUrlDir(rid) + "$3");
	}
	
	public static String getEncoding(File file) {
		byte[] b = new byte[3];
		try {
			InputStream is = new FileInputStream(file);
			is.read(b);
			is.close();
		} catch (IOException e) {
			logger.error("Get encoding error: " + e.getMessage());
		}
		if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
			return "UTF-8";
		} else if ((b[0] == -2 && b[1] == -1) || (b[0] == -1 && b[1] == -2)) {
			return "unicode";
		} else if (b[0] == -26 && b[1] == -75 && b[2] == -117) {	// apple
			return "UTF-8";
		} else if (b[0] == 97 && b[1] == 98 && b[2] == 99) {		// apple
			return "UTF-8";
		} else {
			return "GBK";
		}
	}

	@Override
	public boolean validateIp(String ip) throws DocServiceException {
		return true;
	}
}