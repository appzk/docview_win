package com.idocv.docview.service.impl;


import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.common.ViewType;
import com.idocv.docview.dao.BaseDao;
import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.DocPo;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.CmdUtil;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.ExcelVo;
import com.idocv.docview.vo.ImgVo;
import com.idocv.docview.vo.PPTVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.PdfVo;
import com.idocv.docview.vo.TxtVo;
import com.idocv.docview.vo.WordVo;

@Service
public class ViewServiceImpl implements ViewService, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(ViewServiceImpl.class);
	
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Resource
	private RcUtil rcUtil;
	
	@Resource
	private DocDao docDao;

	private static int WORD_PAGING_LINE_COUNT = 200;
	private static int WORD_PAGING_CHAR_COUNT = 3000;
	
	private @Value("${converter.word2html}")
	String word2Html;
	
	private @Value("${converter.word2pdf}")
	String word2Pdf;

	private @Value("${converter.excel2html}")
	String excel2Html;

	private @Value("${converter.ppt2jpg}")
	String ppt2Jpg;
	
	private @Value("${converter.pdf2img}")
	String pdf2img;

	private @Value("${converter.pdf2html}")
	String pdf2html;

	private @Value("${converter.img2jpg}")
	String img2jpg;

	private @Value("${converter.pdfsign}")
	String pdfSign;

	private static final String encodingString = "(?s)(?i).*?<meta[^>]+?http-equiv=[^>]+?charset=([^\"^>]+?)\"?>.*";
	private static final String encodingStringUtf8 = "(?s)(?i).*?<meta[^>]+?http-equiv=[^>]+?charset=[^>]*?utf([^\"^>]+?)\"?>.*";

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO
	}

	@Override
	public PageVo<WordVo> convertWord2Html(String rid, int start, int limit) throws DocServiceException{
		try {
			convert(rid);
			File htmlFile = new File(rcUtil.getParsePathOfHtml(rid));
			
			// check body
			File bodyFile = new File(rcUtil.getParseDir(rid) + "body.html");
			File styleFile = new File(rcUtil.getParseDir(rid) + "style.css");
			String bodyRaw;
			if (!bodyFile.isFile()) {
				String contentWhole = FileUtils.readFileToString(htmlFile, "GBK");
				if (!contentWhole.matches(encodingString)) {
					contentWhole = FileUtils.readFileToString(htmlFile, "unicode");
				}
				if (contentWhole.matches(encodingStringUtf8)) {
					contentWhole = FileUtils.readFileToString(htmlFile, "utf-8");
				}
				String styleString = contentWhole.replaceFirst("(?s)(?i).*?(<style>)(.*?)</style>.*", "$2");
				bodyRaw = contentWhole.replaceFirst("(?s)(?i).*?(<BODY[^>]*>)(.*?)</BODY>.*", "$2");
				FileUtils.writeStringToFile(styleFile, styleString, "UTF-8");
				FileUtils.writeStringToFile(bodyFile, bodyRaw, "UTF-8");
			} else {
				bodyRaw = FileUtils.readFileToString(bodyFile, "UTF-8");
			}

			// check first page existence
			File firstPageFile = new File(rcUtil.getParseDir(rid) + "1.html");
			String bodyString = bodyRaw;
			bodyString = processStyle(bodyString);

			// split all pages
			if (!firstPageFile.isFile()) {
				List<String> pages = new ArrayList<String>();
				String pageRegex = "(?s)(?i)(.+?)(<span[^>]+?>[\\s]*<br[^>]*?style=[\"|\'][^>]*page-break-before[^>]+>[\\s]*</span>)(.*)(?-i)";
				while (bodyString.matches(pageRegex)) {
					String page = bodyString.replaceFirst(pageRegex, "$1");
					bodyString = bodyString.replaceFirst(pageRegex, "$3");
					pages.add(page);
				}
				pages.add(bodyString);
				int curPageNum = 1;
				// 1. page by manual page breaking
				for (String page : pages) {
					String[] lines = page.split("\r?\n");
					// 2. page by lines
					List<String> subPages = new ArrayList<String>();
					String subPageString = "";
					int count = 0;
					for (int i = 0; i < lines.length; i++, count++) {
						subPageString += lines[i] + "\n";
						if ((count > WORD_PAGING_LINE_COUNT || subPageString
								.length() > WORD_PAGING_CHAR_COUNT)
								&& StringUtils.isNotBlank(lines[i])
								&& lines[i].matches("^(\\S+.*?)?</\\w+>$")) {
							count = 0;
							subPages.add(subPageString.trim());
							subPageString = "";
						}
					}
					if (StringUtils.isNotBlank(subPageString)) {
						subPages.add(subPageString.trim());
					}
					
					if (subPages.size() > 0) {
						String firstSubPageString = subPages.get(0);
						// </div></div><div class="word-page"><div class="word-content">
						firstSubPageString = "<div class=\"page-break-before\"></div>\n" + firstSubPageString;
						subPages.set(0, firstSubPageString);
					}
					// write page content to file
					for (int i = 0; i < subPages.size(); i++) {
						File curPageFile = new File(rcUtil.getParseDir(rid) + curPageNum + ".html");
						FileUtils.writeStringToFile(curPageFile, subPages.get(i), "UTF-8");
						curPageNum++;
					}
				}
			}

			// modify picture path from RELATIVE to ABSOLUTE url.
			// bodyString = processImageUrl(rcUtil.getParseUrlDir(rid), bodyString);
			
			// get page content
			File curPageFile = new File(rcUtil.getParseDir(rid) + start + ".html");
			if (!curPageFile.isFile()) {
				// The last page.
				return new PageVo<WordVo>(null, 0);
			}
			List<String> pages = new ArrayList<String>();
			limit = limit >= 0 ? limit : 0;
			limit = limit == 0 ? Integer.MAX_VALUE : limit;
			int totalPageCount = start;
			for (int i = 0; i < limit; i++) {
				curPageFile = new File(rcUtil.getParseDir(rid) + (start + i) + ".html");
				if (curPageFile.isFile()) {
					totalPageCount = start + i;
					String curPageString = FileUtils.readFileToString(curPageFile, "UTF-8");
					/*
					if (curPageString.startsWith("<div class=\"page-break-before")) {
						String PAGE_BREAKING_STYLE = "</div></div><div class=\"word-page\"><div class=\"word-content\">";
						curPageString = curPageString.replaceFirst("(?s)(?i)<div class=\"page-break-before\"></div>", PAGE_BREAKING_STYLE);
					}
					*/
					curPageString = processImageUrl(rcUtil.getParseUrlDir(rid), curPageString);
					pages.add(curPageString);
				} else {
					break;
				}
			}
			while (new File(rcUtil.getParseDir(rid) + (totalPageCount + 1) + ".html").isFile()) {
				totalPageCount ++;
			}

			List<WordVo> data = new ArrayList<WordVo>();
			// construct vo
			for (int i = 0; i < pages.size(); i++) {
				WordVo word = new WordVo();
				word.setContent("<div id=\"" + (start + i) + "\" class=\"scroll-page\">" + pages.get(i) + "</div>");
				data.add(word);
			}
			PageVo<WordVo> page = new PageVo<WordVo>(data, totalPageCount);
			page.setStyleUrl(rcUtil.getParseUrlDir(rid) + "style.css");
			return page;
		} catch (Exception e) {
			logger.error("convertWord2Html error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}
	
	@Override
	public PageVo<PdfVo> convertWord2PdfStamp(String rid, String stamp, float xPercent, float yPercent) throws DocServiceException {
		try {
			RcUtil.validateRid(rid);
			String src = rcUtil.getPath(rid);
			File srcFile = new File(src);
			if (!srcFile.isFile()) {
				logger.error("文件未找到, rid=" + rid);
				throw new DocServiceException(404, "文件未找到");
			}
			String timeString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			String dest = rcUtil.getParseDir(rid) + "index";
			if (StringUtils.isNotBlank(stamp)) {
				dest += "-stamp-" + timeString;
			}
			dest += ".pdf";
			File destFile = new File(dest);
			String ext = RcUtil.getExt(rid);
			String convertResult = null;
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)) {
				if (!destFile.isFile()) {
					if (StringUtils.isBlank(stamp)) {
						convertResult = CmdUtil.runWindows(word2Pdf, "-src", src, "-dest", dest);
					} else {
						convertResult = CmdUtil.runWindows(word2Pdf, "-src", src, "-dest", dest, "-stamp", stamp.replaceAll("/", "\\\\"), "-x", String.valueOf(xPercent), "-y", String.valueOf(yPercent));
					}
				}
				if (!destFile.isFile()) {
					logger.error("对不起，该文档（" + RcUtil.getUuidByRid(rid)
							+ "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
					throw new DocServiceException("对不起，该文档（"
							+ RcUtil.getUuidByRid(rid)
							+ "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
				}
			} else {
				throw new DocServiceException("该文档不是有效的doc或docx文档！");
			}
			
			String url = rcUtil.getParseUrlDir(rid) + destFile.getName();

			List<PdfVo> data = new ArrayList<PdfVo>();
			// construct vo
			PdfVo pdf = new PdfVo();
			pdf.setUrl(url);
			pdf.setDestFile(destFile);
			data.add(pdf);
			PageVo<PdfVo> page = new PageVo<PdfVo>(data, 1);
			return page;
		} catch (Exception e) {
			logger.error("convertWord2Html error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<ExcelVo> convertExcel2Html(String rid, int start, int limit) throws DocServiceException {
		try {
			convert(rid);
			File rawFilesDir = new File(rcUtil.getParseDir(rid) + "index.files");
			if (!rawFilesDir.isDirectory()) {
				rawFilesDir = new File(rcUtil.getParseDir(rid) + "index_files");
			}
			if (!rawFilesDir.isDirectory()) {
				logger.error("未找到解析目录(" + rid + ")！");
				throw new DocServiceException("未找到解析目录！");
			}

			File[] excelFiles = rawFilesDir.listFiles();
			List<File> sheetFiles = new ArrayList<File>();
			File tabstripFile = null;
			File sheetStyleFile = null;
			for (File excelFile : excelFiles) {
				if (excelFile.getName().matches("sheet\\d+\\.html")) {
					sheetFiles.add(excelFile);
				} else if (excelFile.getName().equalsIgnoreCase("tabstrip.html")) {
					tabstripFile = excelFile;
				} else if (excelFile.getName().endsWith(".css")) {
					sheetStyleFile = excelFile;
				}
			}
			if (CollectionUtils.isEmpty(sheetFiles) || null == tabstripFile) {
				logger.error("未找到Excel解析文件(" + rid + ")！");
				throw new Exception("未找到Excel解析文件！");
			}
			
			// get TITLE(s) and CONTENT(s)
			List<ExcelVo> VoList = new ArrayList<ExcelVo>();
			String titleFileContent = FileUtils.readFileToString(tabstripFile, "GBK");
			for (int i = 0; i < sheetFiles.size(); i++) {
				ExcelVo vo = new ExcelVo();

				// get title
				String title = titleFileContent.replaceFirst("(?s)(?i).+?" + sheetFiles.get(i).getName() + ".+?<font[^>]+>(.+?)</font>.*(?-i)", "$1");
				title = StringUtils.isBlank(title) ? ("Sheet" + (i + 1)) : title;
				// titleList.add(title);

				// get content
				String sheetFileContent = FileUtils.readFileToString(sheetFiles.get(i), "GBK");
				String sheetContent = sheetFileContent.replaceFirst("(?s)(?i).+?<body.+?(<table[^>]+)(>.*</table>).*(?-i)", "$1" + " class=\"table table-condensed table-bordered\"" + "$2");
				sheetContent = sheetContent.replaceFirst("table-layout:fixed;", "");
				sheetContent = processImageUrl(rcUtil.getParseUrlDir(rid), sheetContent);
				sheetContent = processStyle(sheetContent);
				// contentList.add(sheetContent);

				vo.setTitle(title);
				vo.setContent(sheetContent);
				VoList.add(vo);
			}
			PageVo<ExcelVo> page = new PageVo<ExcelVo>(VoList, sheetFiles.size());
			if (null != sheetStyleFile) {
				String styleString = FileUtils.readFileToString(sheetStyleFile, "GBK");
				File destStylePath = new File(rcUtil.getParseDir(rid) + "style.css");
				FileUtils.writeStringToFile(destStylePath, styleString, "UTF-8");
				page.setStyleUrl(rcUtil.getParseUrlDir(rid) + "style.css");
			}
			return page;
		} catch (Exception e) {
			logger.error("convertExcel2Html(" + rid + ") error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage());
		}
	}

	@Override
	public PageVo<PPTVo> convertPPT2Img(String rid, int start, int limit) throws DocServiceException {
		try {
			// get page count
			File[] slide200Files = new File(rcUtil.getParseDir(rid) + IMG_WIDTH_200).listFiles();
			File[] slide1024Files = new File(rcUtil.getParseDir(rid) + IMG_WIDTH_1024).listFiles();
			if (ArrayUtils.isEmpty(slide200Files) || ArrayUtils.isEmpty(slide1024Files)) {
				convert(rid);
				slide200Files = new File(rcUtil.getParseDir(rid) + IMG_WIDTH_200).listFiles();
				slide1024Files = new File(rcUtil.getParseDir(rid) + IMG_WIDTH_1024).listFiles();
			}
			if (ArrayUtils.isEmpty(slide200Files) || ArrayUtils.isEmpty(slide1024Files)) {
				throw new DocServiceException("预览失败，未找到目标文件！");
			}
			
			File imgRatioFile = new File(rcUtil.getParseDir(rid) + IMG_WIDTH_200 + File.separator + "slide1.jpg");
			if (!imgRatioFile.isFile()) {
				throw new DocServiceException("未找到缩略图！");
			}
			BufferedImage imgRatio = ImageIO.read(imgRatioFile);
			float ratio = (float) imgRatio.getHeight() / imgRatio.getWidth();

			List<File> slideImgThumbFiles = new ArrayList<File>();
			List<File> slideImgFiles = new ArrayList<File>();
			
			Map<String, String> titles = new HashMap<String, String>();
			Map<String, String> notes = new HashMap<String, String>();
			for (File slideFile : slide200Files) {
				if (slideFile.getName().toLowerCase().endsWith("jpg")) {
					slideImgThumbFiles.add(slideFile);
				} else if (slideFile.getName().toLowerCase().endsWith("note")) {
					String note = FileUtils.readFileToString(slideFile, "utf-8");
					notes.put(slideFile.getName(), note);
				} else if (slideFile.getName().toLowerCase().endsWith("title")) {
					String title = FileUtils.readFileToString(slideFile, "utf-8");
					titles.put(slideFile.getName(), title);
				}
			}
			for (File slideFile : slide1024Files) {
				if (slideFile.getName().toLowerCase().endsWith("jpg")) {
					slideImgFiles.add(slideFile);
				}
			}

			// sort file
			Collections.sort(slideImgThumbFiles, new FileComparator());
			Collections.sort(slideImgFiles, new FileComparator());

			List<PPTVo> data = new ArrayList<PPTVo>();
			if (!CollectionUtils.isEmpty(slideImgThumbFiles)
					&& !CollectionUtils.isEmpty(slideImgFiles)) {
				for (int i = 0; i < slideImgThumbFiles.size() && i < slideImgFiles.size(); i++) {
					PPTVo ppt = new PPTVo();
					String title = titles.get("slide" + (i + 1) + ".title");
					ppt.setTitle(title);
					String thumbUrl = rcUtil.getParseUrlDir(rid) + IMG_WIDTH_200 + "/" + slideImgThumbFiles.get(i).getName();
					String url = rcUtil.getParseUrlDir(rid) + IMG_WIDTH_1024 + "/" + slideImgFiles.get(i).getName();
					ppt.setThumbUrl(thumbUrl);
					ppt.setUrl(url);
					ppt.setRatio(ratio);
					String note = notes.get("slide" + (i + 1) + ".note");
					ppt.setNote(note);
					data.add(ppt);
				}
			}
			PageVo<PPTVo> page = new PageVo<PPTVo>(data, 1);
			return page;
		} catch (Exception e) {
			logger.error("convertPPT2Html(" + rid + ") error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<TxtVo> convertTxt2Html(String rid, int start, int limit) throws DocServiceException {
		try {
			// check first page existence
			File srcFile = new File(rcUtil.getPath(rid));
			if (!srcFile.isFile()) {
				throw new DocServiceException("原文件未找到！");
			}
			File firstPageFile = new File(rcUtil.getParseDir(rid) + "1.txt");
			if (!firstPageFile.isFile()) {
				String allContent = FileUtils.readFileToString(srcFile, getEncoding(srcFile));
				// content = content.replaceAll("\n|\r\n|\r", "<br />");
				int curPageNum = 1;
				// 1. page by manual page breaking
				String[] lines = allContent.split("\r?\n");
				// 2. page by lines
				List<String> subPages = new ArrayList<String>();
				String subPageString = "";
				int count = 0;
				for (int i = 0; i < lines.length; i++, count++) {
					subPageString += lines[i] + "\n";
					if ((count > WORD_PAGING_LINE_COUNT || subPageString.length() > WORD_PAGING_CHAR_COUNT)) {
						count = 0;
						subPages.add(subPageString.substring(0, subPageString.length() - 1));
						subPageString = "";
					}
				}
				if (StringUtils.isNotBlank(subPageString)) {
					subPages.add(subPageString.substring(0, subPageString.length() - 1));
				}
				
				// write page content to file
				for (int i = 0; i < subPages.size(); i++) {
					File curPageFile = new File(rcUtil.getParseDir(rid) + curPageNum + ".txt");
					FileUtils.writeStringToFile(curPageFile, subPages.get(i), "UTF-8");
					curPageNum++;
				}
			}
			
			// get page content
			File curPageFile = new File(rcUtil.getParseDir(rid) + start + ".txt");
			if (!curPageFile.isFile()) {
				throw new DocServiceException("没有更多内容！");
			}
			
			int totalPageCount = start;
			
			List<String> pages = new ArrayList<String>();
			limit = limit >= 0 ? limit : 0;
			limit = limit == 0 ? Integer.MAX_VALUE : limit;
			for (int i = 0; i < limit; i++) {
				curPageFile = new File(rcUtil.getParseDir(rid) + (start + i) + ".txt");
				if (curPageFile.isFile()) {
					totalPageCount = start + i;
					String curPageString = FileUtils.readFileToString(curPageFile, "UTF-8");
					pages.add(curPageString);
				} else {
					break;
				}
			}
			
			while (new File(rcUtil.getParseDir(rid) + (totalPageCount + 1) + ".txt").isFile()) {
				totalPageCount ++;
			}
			
			List<TxtVo> data = new ArrayList<TxtVo>();
			// construct vo
			for (int i = 0; i < pages.size(); i++) {
				// vo.setContent("<div id=\"" + (start + i) + "\" class=\"scroll-page\"><pre>" + pages.get(i) + "</pre></div>");
				String content = pages.get(i);
				if (StringUtils.isNotEmpty(content)) {
					TxtVo vo = new TxtVo();
					String escapeContent = StringEscapeUtils.escapeHtml(content).replaceAll("(\r)?\n", "<br />");
					vo.setContent("<div id=\"" + (start + i) + "\" class=\"scroll-page\">" + escapeContent + "</div>");
					data.add(vo);
				}
			}
			PageVo<TxtVo> page = new PageVo<TxtVo>(data, totalPageCount);
			return page;
		} catch (IOException e) {
			logger.error("getTxtContent error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<PdfVo> convertPdf2Img(String rid, int start, int limit) throws DocServiceException {
		try {
			// get page count
			File[] pngFiles = new File(rcUtil.getParseDir(rid) + PDF_TO_IMAGE_TYPE).listFiles();
			if (ArrayUtils.isEmpty(pngFiles)) {
				convert(rid);
				pngFiles = new File(rcUtil.getParseDir(rid) + PDF_TO_IMAGE_TYPE).listFiles();
			}
			if (ArrayUtils.isEmpty(pngFiles)) {
				throw new DocServiceException("预览失败，不是一个PDF文件或该文件已损坏！");
			}
			
			List<File> pdfPageFiles = new ArrayList<File>();
			for (File pngFile : pngFiles) {
				pdfPageFiles.add(pngFile);
			}

			// sort file
			Collections.sort(pdfPageFiles, new FileComparator());

			List<PdfVo> data = new ArrayList<PdfVo>();
			if (!CollectionUtils.isEmpty(pdfPageFiles)) {
				for (int i = 0; i < pdfPageFiles.size(); i++) {
					PdfVo pdf = new PdfVo();
					String url = rcUtil.getParseUrlDir(rid) + PDF_TO_IMAGE_TYPE + "/" + pdfPageFiles.get(i).getName();
					pdf.setUrl(url);
					data.add(pdf);
				}
			}
			PageVo<PdfVo> page = new PageVo<PdfVo>(data, pdfPageFiles.size());
			return page;
		} catch (Exception e) {
			logger.error("convertPPT2Html(" + rid + ") error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<PdfVo> convertPdf2Html(String rid, int start, int limit) throws DocServiceException {
		try {
			convert(rid);
			File htmlFile = new File(rcUtil.getParsePathOfHtml(rid));
			String htmlRaw = FileUtils.readFileToString(htmlFile, "UTF-8");

			// check first page existence
			File firstPageFile = new File(rcUtil.getParseDir(rid) + "1.html");
			String bodyString = htmlRaw;

			// split all pages
			if (!firstPageFile.isFile()) {
				List<String> pages = new ArrayList<String>();
				String pageRegex = "(?s)(?i)(.*?)(<div class=\"pd w0 h0\">.+?></div></div></div>)(.*)(?-i)";
				while (bodyString.matches(pageRegex)) {
					String page = bodyString.replaceFirst(pageRegex, "$2");
					bodyString = bodyString.replaceFirst(pageRegex, "$3");
					pages.add(page);
				}
				if (CollectionUtils.isEmpty(pages)) {
					logger.error("未找到页面内容（" + rid + "）！");
					throw new DocServiceException("未找到页面内容！");
				}

				// save pages
				for (int i = 0; i < pages.size(); i++) {
					File curPageFile = new File(rcUtil.getParseDir(rid) + (i + 1) + ".html");
					FileUtils.writeStringToFile(curPageFile, pages.get(i), "UTF-8");
				}
			}
			
			// get page content
			File curPageFile = new File(rcUtil.getParseDir(rid) + start + ".html");
			if (!curPageFile.isFile()) {
				// The last page.
				return new PageVo<PdfVo>(null, 0);
			}
			List<String> pages = new ArrayList<String>();
			limit = limit >= 0 ? limit : 0;
			limit = limit == 0 ? Integer.MAX_VALUE : limit;
			int totalPageCount = start;
			for (int i = 0; i < limit; i++) {
				curPageFile = new File(rcUtil.getParseDir(rid) + (start + i) + ".html");
				if (curPageFile.isFile() && new File(rcUtil.getParseDir(rid) + "bg" + (i + 1) + ".png").isFile()) {
					totalPageCount = start + i;
					String curPageString = FileUtils.readFileToString(curPageFile, "UTF-8");
					curPageString = processImageUrlOfPdf(rcUtil.getParseUrlDir(rid), curPageString);
					pages.add(curPageString);
				} else {
					break;
				}
			}
			while (new File(rcUtil.getParseDir(rid) + "bg" + (totalPageCount + 1) + ".png").isFile()) {
				totalPageCount ++;
			}

			List<PdfVo> data = new ArrayList<PdfVo>();
			// construct vo
			for (int i = 0; i < pages.size(); i++) {
				PdfVo pdf = new PdfVo();
				pdf.setContent("<div id=\"" + (start + i) + "\" class=\"scroll-page\">" + pages.get(i) + "</div>");
				pdf.setBackground(rcUtil.getParseUrlDir(rid) + "bg" + (i + 1) + ".png");
				data.add(pdf);
			}
			PageVo<PdfVo> page = new PageVo<PdfVo>(data, totalPageCount);
			page.setStyleUrl(rcUtil.getParseUrlDir(rid) + RcUtil.getFileNameWithoutExt(rid) + ".css");
			page.setUrl(rcUtil.getParseUrlDir(rid) + "index.html");
			return page;
		} catch (Exception e) {
			logger.error("convertPdf2Html error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}
	
	@Override
	public PageVo<ImgVo> convertImage2Jpg(String rid) throws DocServiceException {
		try {
			convert(rid);
			String ext = RcUtil.getExt(rid);
			String viewName = "index.jpg";
			if ("png".equalsIgnoreCase(ext)) {
				viewName = "index.png";
			} else if ("gif".equalsIgnoreCase(ext)) {
				viewName = "index.gif";
			}

			// get page count
			File[] destFiles = new File(rcUtil.getParseDir(rid)).listFiles();
			if (ArrayUtils.isEmpty(destFiles)) {
				convert(rid);
				destFiles = new File(rcUtil.getParseDir(rid)).listFiles();
			}
			if (ArrayUtils.isEmpty(destFiles)) {
				throw new DocServiceException("预览失败，未找到目标文件！");
			}

			List<File> destFilesList = new ArrayList<File>();
			for (File pngFile : destFiles) {
				destFilesList.add(pngFile);
			}

			// sort file
			Collections.sort(destFilesList, new FileComparator());

			List<ImgVo> data = new ArrayList<ImgVo>();
			if (!CollectionUtils.isEmpty(destFilesList)) {
				for (int i = 0; i < destFilesList.size(); i++) {
					ImgVo imgVo = new ImgVo();
					String url = rcUtil.getParseUrlDir(rid) + destFilesList.get(i).getName();
					imgVo.setUrl(url);
					data.add(imgVo);
				}
			}
			PageVo<ImgVo> page = new PageVo<ImgVo>(data, destFilesList.size());
			page.setUrl(rcUtil.getParseUrlDir(rid) + viewName);
			return page;
		} catch (Exception e) {
			logger.error("convertImage2Jpg error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	public boolean convert(String rid) throws DocServiceException {
		String uuid = RcUtil.getUuidByRid(rid);
		DocPo docPo;
		try {
			docPo = docDao.get(rid, false);
			if (null == docPo) {
				logger.error("获取文档元数据失败(" + rid + ")");
				throw new DocServiceException("获取文档元数据失败(" + rid + ")");
			}
			int convertStatus = docPo.getConvert();
			if (convertStatus < 0) {
				logger.error("对不起，该文档" + rid + "暂无法预览，请查看其它文档！");
				throw new DocServiceException("对不起，该文档" + uuid
						+ "暂无法预览，请查看其它文档！");
			}
			if (1 == convertStatus) {
				return true;
			}
			if (2 == convertStatus) {
				String utimeString = docPo.getUtime();
				Date utime = df.parse(utimeString);
				Date nowBefore3Min = DateUtils.addMinutes(new Date(), -3);
				if (utime.before(nowBefore3Min)) {
					docDao.updateFieldById(rid, BaseDao.STATUS_CONVERT, BaseDao.STATUS_CONVERT_FAIL);
					logger.error("对不起，该文档（" + rid
							+ "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开并重新上传！");
					throw new DocServiceException("对不起，该文档（" + uuid
							+ "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开并重新上传！");
				} else {
					Thread.sleep(3000);
					return convert(rid);
				}
			}
		} catch (Exception e) {
			logger.error("转换文档失败(" + rid + "):" + e.getMessage());
			throw new DocServiceException("转换文档失败(" + uuid + "):"
					+ e.getMessage());
		}

		String ext = RcUtil.getExt(rid);
		if (!rcUtil.isSupportView(ext)) {
			try {
				docDao.updateFieldById(rid, BaseDao.STATUS_CONVERT, BaseDao.STATUS_CONVERT_NOT_SUPPORT);
			} catch (Exception e) {
				logger.error("update field of " + rid + " error: " + e.getMessage());
			}
			throw new DocServiceException("不支持" + ext + "类型文件预览，详情请联系管理员！");
		}
		long startConvert = System.currentTimeMillis();
		try {
			String src = rcUtil.getPath(rid);
			File srcFile = new File(src);
			String dest = rcUtil.getParsePathOfHtml(rid);
			File destFile = new File(dest);
			if (!srcFile.isFile()) {
				logger.error("文件未找到, rid=" + rid);
				throw new DocServiceException(404, "文件未找到");
			}
			docDao.updateFieldById(rid, BaseDao.STATUS_CONVERT, BaseDao.STATUS_CONVERT_CONVERTING);
			String convertResult = "";
			if (ViewType.WORD == ViewType.getViewType(ext)) {
				if (!destFile.isFile()) {
					convertResult = CmdUtil.runWindows(word2Html, src, dest);
				}
				if (!destFile.isFile()) {
					logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
					throw new DocServiceException("对不起，该文档（"
							+ RcUtil.getUuidByRid(rid)
							+ "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
				}
			} else if (ViewType.EXCEL == ViewType.getViewType(ext)) {
				if (!destFile.isFile()) {
					convertResult = CmdUtil.runWindows(excel2Html, src, dest);
				}
				if (!destFile.isFile()) {
					logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
					throw new DocServiceException("对不起，该文档（"
							+ RcUtil.getUuidByRid(rid)
							+ "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
				}
			} else if (ViewType.PPT == ViewType.getViewType(ext)) {
				dest = rcUtil.getParseDir(rid);
				destFile = new File(dest);
				if (ArrayUtils.isEmpty(new File(dest + IMG_WIDTH_200).listFiles())) {
					convertResult += CmdUtil.runWindows(ppt2Jpg, src, dest, "false", IMG_WIDTH_200);
				}
				if (ArrayUtils.isEmpty(new File(dest + IMG_WIDTH_1024).listFiles())) {
					convertResult += CmdUtil.runWindows(ppt2Jpg, src, dest, "false", IMG_WIDTH_1024);
				}
				if (ArrayUtils.isEmpty(new File(dest + IMG_WIDTH_200).listFiles()) || ArrayUtils.isEmpty(new File(dest + IMG_WIDTH_1024).listFiles())) {
					logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
					throw new DocServiceException("对不起，该文档（"
							+ RcUtil.getUuidByRid(rid)
							+ "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
				}
			} else if (ViewType.PDF == ViewType.getViewType(ext)) {
				String destDir = rcUtil.getParseDirOfPdf2Png(rid);	// Directory MUST exist(Apache PDFBox)
				String destFirstPage = destDir + "1." + PDF_TO_IMAGE_TYPE;
				if (!new File(destFirstPage).isFile()) {
					// String convertInfo = CmdUtil.runWindows("java", "-jar", pdf2img, "PDFToImage", "-imageType", PDF_TO_IMAGE_TYPE, "-outputPrefix", destDir, src);
					convertResult = CmdUtil.runWindows(pdf2img, "-q", "-dNOPAUSE", "-dBATCH", "-sDEVICE=png16m", "-sPAPERSIZE=a2", "-dPDFFitPage", "-dUseCropBox", "-sOutputFile=" + destDir + "%d.png", src);
				}
				if (!new File(destFirstPage).isFile()) {
					logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
					throw new DocServiceException("对不起，该文档（"
							+ RcUtil.getUuidByRid(rid)
							+ "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
				}
				/* pdf2htmlEx
				if (!destFile.isFile()) {
					destDir = destDir.replaceAll("/", "\\\\");
					destDir = destDir.substring(0, destDir.length() - 1);
					String convertInfo = CmdUtil.runWindows(pdf2html, "--dest-dir", destDir.replaceAll("/", "\\\\"), "--embed", "cfijo", "--fallback", "1", src.replaceAll("\\\\", "/"), "index.html");
					logger.info("Convert info: \n" + convertInfo);
				}
				*/
			} else if (ViewType.TXT == ViewType.getViewType(ext)) {
				// do nothing.
			} else if (ViewType.IMG == ViewType.getViewType(ext)) {
				dest = rcUtil.getParseDir(rid);
				String destName = "index.jpg";
				if ("png".equalsIgnoreCase(ext)) {
					destName = "index.png";
				} else if ("gif".equalsIgnoreCase(ext)) {
					destName = "index.gif";
				}
				String destPath = dest + destName;
				File destDir = new File(dest);
				if (ArrayUtils.isEmpty(destDir.listFiles())) {
					convertResult = CmdUtil.runWindows(img2jpg, "-resize", "1000x", src, destPath);
				}
				if (ArrayUtils.isEmpty(destDir.listFiles())) {
					logger.error("[CONVERT ERROR] " + rid + " - "
							+ convertResult);
					throw new DocServiceException("对不起，该图片文件（"
							+ RcUtil.getUuidByRid(rid) + "）暂无法预览，请确认能正常打开！");
				}
			} else {
				logger.error("目前不支持（" + ext + "）格式！");
				throw new DocServiceException("目前不支持（" + ext + "）格式！");
			}
			logger.debug("[CONVERT DEBUG] " + rid + " - " + convertResult);
			try {
				docDao.updateFieldById(rid, BaseDao.STATUS_CONVERT, BaseDao.STATUS_CONVERT_SUCCESS);
			} catch (DBException e) {
				logger.error("update field of " + rid + " error: " + e.getMessage());
			}
		} catch (Exception e) {
			logger.error("[CONVERT ERROR] " + rid + " - " + e.getMessage());
			try {
				docDao.updateFieldById(rid, BaseDao.STATUS_CONVERT, BaseDao.STATUS_CONVERT_FAIL);
			} catch (Exception eu) {
				logger.error("update field of " + rid + " error: " + eu.getMessage());
				throw new DocServiceException(e.getMessage());
			}
			throw new DocServiceException(e.getMessage());
		}
		long endConvert = System.currentTimeMillis();
		long size = RcUtil.getSizeByRid(rid);
		long elapse = endConvert - startConvert;
		elapse = elapse <= 0 ? 1 : elapse;
		double rate = (double) size / elapse;
		rate = new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP).doubleValue();
		logger.info("[CONVERT SUCCESS] " + rid + " of size " + size + " within " + (endConvert - startConvert) + " milisecond(s), convert rate: " + rate + " k/s");
		return true;
	}

	/**
	 * remove unnecessary content styles
	 * 
	 * @param content
	 * @return
	 */
	private static String processStyle(String content) {
		return content.replaceAll("position:[^;]{5,12};", "")			// remove position style
					  .replaceAll("margin-left:[^;]{1,10};", "")		// remove margin left
					  .replaceAll("margin-right:[^;]{1,10};", "")		// remove position right
					  .replaceAll("</?div[^>]*>", "")					// remove div
					  .replaceAll("text-indent:[^;^']+;?", "");			// remove text-indent
	}

	/**
	 * modify picture path from RELATIVE to ABSOLUTE url.
	 * @param prefix the prefix to be added to image SRC parameter.<br>
	 * 			e.g.
	 * 			<ul>
	 * 				<li>Word file, use: rcUtil.getParseUrlDir(rid)</li>
	 * 				<li>Excel sheet file, use: rcUtil.getParseUrlDir(rid) + "index.files/"</li>
	 * 			</ul>
	 * @param content
	 * @return
	 */
	public String processImageUrl(String prefix, String content) throws DocServiceException {
		String regex = "(?s)(?i)(<img[^>]+?src=\"?)([^>]*?)((index.files[/\\\\])?)([^>]+?>)(?-i)";
		return content.replaceAll(regex, "$1" + prefix + "$3$5");
	}
	
	public String processImageUrlOfPdf(String prefix, String content) throws DocServiceException {
		return content.replaceAll("(?s)(?i)(<img[^>]+?src=\"?)([^>]+?>)(?-i)", "$1" + prefix + "$2");
	}
	
	public static void write2File(File file, String content) throws IOException {
		FileUtils.writeStringToFile(file, content, "UTF-8");
	}

	public static String getEncoding(File file) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		BufferedInputStream bis = null;
		try {
			boolean checked = false;
			bis = new BufferedInputStream(new FileInputStream(file));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1) {
				return charset; // 文件编码为 ANSI
			} else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE"; // 文件编码为 Unicode
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE"; // 文件编码为 Unicode big endian
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8"; // 文件编码为 UTF-8
				checked = true;
			}
			bis.reset();
			if (!checked) {
				while ((read = bis.read()) != -1) {
					if (read >= 0xF0) {
						break;
					}
					if (0x80 <= read && read <= 0xBF) {
						// 单独出现BF以下的，也算是GBK
						break;
					}
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							// 双字节 (0xC0 - 0xDF)
							// (0x80
							// - 0xBF),也可能在GB编码内
							continue;
						} else {
							break;
						}
					} else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else {
								break;
							}
						} else {
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return charset;
	}

	@Override
	public boolean validateIp(String ip) throws DocServiceException {
		return true;
	}
}

class FileComparator implements Comparator<File> {
	
	@Override
	public int compare(File o1, File o2) {
		String name1 = o1.getName();
		String name2 = o2.getName();
		String nameRegex = "[^\\d]*?(\\d+).*";
		try {
			int nameDigit1 = Integer.valueOf(name1.replaceFirst(nameRegex, "$1"));
			int nameDigit2 = Integer.valueOf(name2.replaceFirst(nameRegex, "$1"));
			return nameDigit1 == nameDigit2 ? 0 : (nameDigit1 > nameDigit2 ? 1 : -1);
		} catch (NumberFormatException e) {
			return 1;
		}
	}

}