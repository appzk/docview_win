package com.idocv.docview.service.impl;


import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
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
import com.idocv.docview.vo.AudioVo;
import com.idocv.docview.vo.CadVo;
import com.idocv.docview.vo.ExcelVo;
import com.idocv.docview.vo.ImgVo;
import com.idocv.docview.vo.PPTVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.PdfVo;
import com.idocv.docview.vo.TxtVo;
import com.idocv.docview.vo.WordVo;
import com.idocv.docview.vo.ZipVo;

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
	
	private @Value("${converter.excel2pdf}")
	String excel2Pdf;

	private @Value("${converter.ppt2jpg}")
	String ppt2Jpg;
	
	private @Value("${converter.pdf2img}")
	String pdf2img;

	private @Value("${converter.pdf2html}")
	String pdf2html;

	private @Value("${converter.img2jpg}")
	String img2jpg;
	
	private @Value("${converter.audio2mp3}")
	String audio2mp3;
	
	private @Value("${converter.zip2file}")
	String zip2file;
	
	private @Value("${converter.cad2img}")
	String cad2img;

	private @Value("${view.page.style.word}")
	String viewPageStyleWord;
	
	private @Value("${view.page.style.excel}")
	String viewPageStyleExcel;
	
	private @Value("${view.page.style.pdf}")
	String viewPageStylePdf;

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO
	}
	
	@Override
	public PageVo<WordVo> convertWord2HtmlAll(String rid) throws DocServiceException{
		try {
			convert(rid);
			File htmlFile = new File(rcUtil.getParsePathOfHtml(rid));
			
			// check body
			File bodyFile = new File(rcUtil.getParseDir(rid) + "body.html");
			File styleFile = new File(rcUtil.getParseDir(rid) + "style.css");
			String bodyString;
			if (!bodyFile.isFile()) {
				String contentWhole = FileUtils.readFileToString(htmlFile, "UTF-8");
				String styleString = contentWhole.replaceFirst("(?s)(?i).*?(<style>)(.*?)</style>.*", "$2");
				bodyString = contentWhole.replaceFirst("(?s)(?i).*?(<BODY[^>]*>)(.*?)</BODY>.*", "$2");
				bodyString = processStyle(bodyString);
				FileUtils.writeStringToFile(styleFile, styleString, "UTF-8");
				FileUtils.writeStringToFile(bodyFile, bodyString, "UTF-8");
			} else {
				bodyString = FileUtils.readFileToString(bodyFile, "UTF-8");
			}

			bodyString = processImageUrl(rcUtil.getParseUrlDir(rid), bodyString);
			
			List<WordVo> data = new ArrayList<WordVo>();
			WordVo word = new WordVo();
			word.setContent(bodyString);
			data.add(word);
			PageVo<WordVo> page = new PageVo<WordVo>(data, 1);
			page.setStyleUrl(rcUtil.getParseUrlDir(rid) + "style.css");
			return page;
		} catch (Exception e) {
			logger.error("convertWord2Html error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<WordVo> convertWord2Html(String rid, int start, int limit) throws DocServiceException{
		try {
			convert(rid);
			File htmlFile = new File(rcUtil.getParsePathOfHtml(rid));
			
			// check body
			File bodyFile = new File(rcUtil.getParseDir(rid) + "body.html");
			File styleFile = new File(rcUtil.getParseDir(rid) + "style.css");
			String bodyString;
			if (!bodyFile.isFile()) {
				String contentWhole = FileUtils.readFileToString(htmlFile, "UTF-8");
				String styleString = contentWhole.replaceFirst("(?s)(?i).*?(<style>)(.*?)</style>.*", "$2");
				bodyString = contentWhole.replaceFirst("(?s)(?i).*?(<BODY[^>]*>)(.*?)</BODY>.*", "$2");
				bodyString = processStyle(bodyString);
				FileUtils.writeStringToFile(styleFile, styleString, "UTF-8");
				FileUtils.writeStringToFile(bodyFile, bodyString, "UTF-8");
			} else {
				bodyString = FileUtils.readFileToString(bodyFile, "UTF-8");
			}

			// check first page existence
			File firstPageFile = new File(rcUtil.getParseDir(rid) + "1.html");

			// split all pages
			if (!firstPageFile.isFile()) {
				List<String> pages = new ArrayList<String>();
				
				// get titles
				List<String> titles = new ArrayList<String>();
				String titleString = bodyString;
				String titleRegex = "(?s)(?i)(.*?)(<(h(\\d+))[^>]*?>(.*?)</\\3>)(.*)(?-i)";
				StringBuffer anchorContent = new StringBuffer();
				int titleCount = 0;
				while (titleString.matches(titleRegex)) {
					Integer titleLevel = Integer.valueOf(titleString.replaceFirst(titleRegex, "$4"));
					String title = titleString.replaceFirst(titleRegex, "$5").replaceAll("<[^>]+>", "");
					anchorContent.append(titleString.replaceFirst(titleRegex, "$1" + "<a id=\"nav-title-" + titleCount + "\"></a>" + "$2"));
					titleCount++;
					titleString = titleString.replaceFirst(titleRegex, "$6");
					for (int li = 1; li < titleLevel; li++) {
						title = "->" + title;
					}
					
					// 格式标题，注意正则中间的空格，第一个是160，第二个是32
					title = StringEscapeUtils.unescapeHtml(title)
							.replaceAll("[\\s \r\n]+", " ").trim();
					titles.add(title);
				}
				if (anchorContent.length() > 0) {
					anchorContent.append(titleString);
					bodyString = anchorContent.toString();
				}

				// save titles
				File titlesFile = new File(rcUtil.getParseDir(rid) + "titles.txt");
				FileUtils.writeLines(titlesFile, "UTF-8", titles, "\n");
				
				// get pages
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
				word.setContent("<div id=\"" + (start + i) + "\" class=\"scroll-page\" >" + pages.get(i) + "</div>");
				data.add(word);
			}
			PageVo<WordVo> page = new PageVo<WordVo>(data, totalPageCount);
			page.setStyleUrl(rcUtil.getParseUrlDir(rid) + "style.css");
			
			try {
				List<String> titles = FileUtils.readLines(new File(rcUtil.getParseDir(rid) + "titles.txt"), "UTF-8");
				page.setTitles(titles);
			} catch (Exception e) {
				logger.warn("获取标题失败：" + e.getMessage());
			}
			return page;
		} catch (Exception e) {
			logger.error("convertWord2Html error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}
	
	@Override
	public PageVo<WordVo> convertWord2Img(String rid, int start, int limit) throws DocServiceException {
		try {
			// get page count
			File[] pngFiles = new File(rcUtil.getParseDir(rid) + PDF_TO_IMAGE_TYPE).listFiles();
			if (ArrayUtils.isEmpty(pngFiles)) {
				convert(rid);
				pngFiles = new File(rcUtil.getParseDir(rid) + PDF_TO_IMAGE_TYPE).listFiles();
			}
			
			// double check & convert word->pdf-png
			if (ArrayUtils.isEmpty(pngFiles)) {
				String src = rcUtil.getPath(rid);
				String tmpPdfPath = rcUtil.getParseDir(rid) + RcUtil.getFileNameWithoutExt(rid) + ".pdf";
				File tmpPdfFile = new File(tmpPdfPath);
				
				// two steps to convert WORD to PNG
				// step 1. convert WORD to PDF
				String convertResult = "";
				if (!tmpPdfFile.isFile()) {
					convertResult += CmdUtil.runWindows(word2Pdf, "-src", src, "-dest", tmpPdfPath);
					if (!tmpPdfFile.isFile()) {
						logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
						throw new DocServiceException("对不起，该文档（"
								+ RcUtil.getUuidByRid(rid) + "）暂无法预览，详情请联系管理员！");
					}
				}
				
				// step 2. convert PDF to PNG pictures
				String pngDestDir = rcUtil.getParseDirOfPdf2Png(rid);	// Directory MUST exist(Apache PDFBox)
				String pngDestFirstPage = pngDestDir + "1." + PDF_TO_IMAGE_TYPE;
				if (!new File(pngDestFirstPage).isFile()) {
					// convertResult += CmdUtil.runWindows(pdf2img, "-q", "-dNOPAUSE", "-dBATCH", "-sDEVICE=png16m", "-sPAPERSIZE=a3", "-dPDFFitPage", "-dUseCropBox", "-sOutputFile=" + pngDestDir + "%d.png", tmpPdfPath);
					convertResult += convertPdf2Img(pdf2img, pngDestDir, tmpPdfPath);
				}
				if (!new File(pngDestFirstPage).isFile()) {
					logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
					throw new DocServiceException("对不起，该文档（"
							+ RcUtil.getUuidByRid(rid) + "）暂无法预览，详情请联系管理员！");
				}
				pngFiles = new File(rcUtil.getParseDir(rid) + PDF_TO_IMAGE_TYPE).listFiles();
			}
			if (ArrayUtils.isEmpty(pngFiles)) {
				logger.error("convertWord2Img(" + rid
						+ ") error: 预览失败，该word文档无法生成目标PNG文件或该文件已损坏！");
				throw new DocServiceException("预览失败，该word文档无法生成目标PNG文件或该文件已损坏！");
			}
			
			List<File> pdfPageFiles = new ArrayList<File>();
			for (File pngFile : pngFiles) {
				pdfPageFiles.add(pngFile);
			}

			// sort file
			Collections.sort(pdfPageFiles, new FileComparator());

			List<WordVo> data = new ArrayList<WordVo>();
			if (!CollectionUtils.isEmpty(pdfPageFiles)) {
				for (int i = 0; i < pdfPageFiles.size(); i++) {
					WordVo pdf = new WordVo();
					String url = rcUtil.getParseUrlDir(rid) + PDF_TO_IMAGE_TYPE + "/" + pdfPageFiles.get(i).getName();
					pdf.setUrl(url);
					data.add(pdf);
				}
			}
			PageVo<WordVo> page = new PageVo<WordVo>(data, pdfPageFiles.size());
			return page;
		} catch (Exception e) {
			logger.error("convertPPT2Html(" + rid + ") error: ", e.fillInStackTrace());
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
			String subDirName = "index.files";
			File rawFilesDir = new File(rcUtil.getParseDir(rid) + subDirName);
			if (!rawFilesDir.isDirectory()) {
				subDirName = "index_files";
				rawFilesDir = new File(rcUtil.getParseDir(rid) + subDirName);
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
			String titleFileContent = FileUtils.readFileToString(tabstripFile, "UTF-8");
			for (int i = 0; i < sheetFiles.size(); i++) {
				ExcelVo vo = new ExcelVo();

				// get title
				String title = titleFileContent.replaceFirst("(?s)(?i).+?" + sheetFiles.get(i).getName() + ".+?<font[^>]+>(.+?)</font>.*(?-i)", "$1");
				title = StringUtils.isBlank(title) ? ("Sheet" + (i + 1)) : title;
				// titleList.add(title);

				// get content
				String sheetFileContent = FileUtils.readFileToString(sheetFiles.get(i), "UTF-8");
				String sheetContent = sheetFileContent.replaceFirst("(?s)(?i).+?<body.+?(<table[^>]+)(>.*</table>).*(?-i)", "$1" + " class=\"table table-condensed table-bordered\"" + "$2");
				sheetContent = sheetContent.replaceFirst("table-layout:fixed;", "");
				sheetContent = processImageUrl(rcUtil.getParseUrlDir(rid) + subDirName + "/", sheetContent);
				sheetContent = processStyle(sheetContent);
				// contentList.add(sheetContent);

				vo.setTitle(title);
				vo.setContent(sheetContent);
				VoList.add(vo);
			}
			PageVo<ExcelVo> page = new PageVo<ExcelVo>(VoList, sheetFiles.size());
			if (null != sheetStyleFile) {
				String styleString = FileUtils.readFileToString(sheetStyleFile, "UTF-8");
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
	public PageVo<ExcelVo> convertExcel2Img(String rid, int start, int limit) throws DocServiceException {
		try {
			// get page count
			File[] pngFiles = new File(rcUtil.getParseDir(rid) + PDF_TO_IMAGE_TYPE).listFiles();
			if (ArrayUtils.isEmpty(pngFiles)) {
				convert(rid);
				pngFiles = new File(rcUtil.getParseDir(rid) + PDF_TO_IMAGE_TYPE).listFiles();
			}
			if (ArrayUtils.isEmpty(pngFiles)) {
				logger.error("convertExcel2Img(" + rid
						+ ") error: 预览失败，该excel文档无法生成目标PNG文件或该文件已损坏！");
				throw new DocServiceException(
						"预览失败，该excel文档无法生成目标PNG文件或该文件已损坏！");
			}
			
			List<File> pdfPageFiles = new ArrayList<File>();
			for (File pngFile : pngFiles) {
				pdfPageFiles.add(pngFile);
			}

			// sort file
			Collections.sort(pdfPageFiles, new FileComparator());

			List<ExcelVo> data = new ArrayList<ExcelVo>();
			if (!CollectionUtils.isEmpty(pdfPageFiles)) {
				for (int i = 0; i < pdfPageFiles.size(); i++) {
					ExcelVo pdf = new ExcelVo();
					String url = rcUtil.getParseUrlDir(rid) + PDF_TO_IMAGE_TYPE + "/" + pdfPageFiles.get(i).getName();
					pdf.setUrl(url);
					data.add(pdf);
				}
			}
			PageVo<ExcelVo> page = new PageVo<ExcelVo>(data, pdfPageFiles.size());
			return page;
		} catch (Exception e) {
			logger.error("convertExcel2Img(" + rid + ") error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
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
			Map<String, String> texts = new HashMap<String, String>();
			for (File slideFile : slide200Files) {
				if (slideFile.getName().toLowerCase().endsWith("jpg")) {
					slideImgThumbFiles.add(slideFile);
				} else if (slideFile.getName().toLowerCase().endsWith("note")) {
					String note = FileUtils.readFileToString(slideFile, "utf-8");
					notes.put(slideFile.getName(), note);
				} else if (slideFile.getName().toLowerCase().endsWith("title")) {
					String title = FileUtils.readFileToString(slideFile, "utf-8");
					titles.put(slideFile.getName(), title);
				} else if (slideFile.getName().toLowerCase().endsWith("text")) {
					String text = FileUtils.readFileToString(slideFile, "utf-8");
					texts.put(slideFile.getName(), text);
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
			if (!CollectionUtils.isEmpty(slideImgThumbFiles) && !CollectionUtils.isEmpty(slideImgFiles)) {
				for (int i = 0; i < slideImgThumbFiles.size() && i < slideImgFiles.size(); i++) {
					PPTVo ppt = new PPTVo();

					// title
					String title = titles.get("slide" + (i + 1) + ".title");
					ppt.setTitle(title);
					String thumbUrl = rcUtil.getParseUrlDir(rid) + IMG_WIDTH_200 + "/" + slideImgThumbFiles.get(i).getName();
					String url = rcUtil.getParseUrlDir(rid) + IMG_WIDTH_1024 + "/" + slideImgFiles.get(i).getName();
					ppt.setThumbUrl(thumbUrl);
					ppt.setUrl(url);
					ppt.setRatio(ratio);

					// note
					String note = notes.get("slide" + (i + 1) + ".note");
					ppt.setNote(note);

					// text
					String text = texts.get("slide" + (i + 1) + ".text");
					ppt.setText(text);

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
			
			// double check & convert pdf->png
			if (ArrayUtils.isEmpty(pngFiles)) {
				String destDir = rcUtil.getParseDirOfPdf2Png(rid);	// Directory MUST exist(Apache PDFBox)
				String destFirstPage = destDir + "1." + PDF_TO_IMAGE_TYPE;
				String convertResult = "";
				if (!new File(destFirstPage).isFile()) {
					String src = rcUtil.getPath(rid);
					// convertResult = CmdUtil.runWindows(pdf2img, "-q", "-dNOPAUSE", "-dBATCH", "-sDEVICE=png16m", "-sPAPERSIZE=a3", "-dPDFFitPage", "-dUseCropBox", "-sOutputFile=" + destDir + "%d.png", src);
					convertResult = convertPdf2Img(pdf2img, destDir, src);
					pngFiles = new File(rcUtil.getParseDir(rid) + PDF_TO_IMAGE_TYPE).listFiles();
				}
				if (ArrayUtils.isEmpty(pngFiles)) {
					logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
					throw new DocServiceException("对不起，该文档（"
							+ RcUtil.getUuidByRid(rid) + "）暂无法预览，详情请联系管理员！");
				}
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

			String destDir = rcUtil.getParseDirOfPdf2Html(rid);

			// double check & convert pdf->html
			File destIndexFile = new File(destDir, "index.html");
			if (!destIndexFile.isFile()) {
				destDir = destDir.replaceAll("/", "\\\\");
				destDir = destDir.endsWith("\\") ? destDir.substring(0, destDir.length() - 1) : destDir;
				String src = rcUtil.getPath(rid);
				String convertResult = CmdUtil.runWindows(pdf2html, "--embed", "cfijo", "--split-pages", "1", "--page-filename", "%d.page", "--zoom", "1.3", "--dest-dir", destDir, src.replaceAll("\\\\", "/"), "index.html");
				if (!destIndexFile.isFile()) {
					logger.error("convertPdf2Html(" + rid
							+ ") error: 预览失败，该pdf文档无法生成目标html文件或该文件已损坏，转换结果："
							+ convertResult);
					throw new DocServiceException(
							"预览失败，该pdf文档无法生成目标html文件或该文件已损坏！");
				}
			}

			// check first page existence
			File firstPageFile = new File(rcUtil.getParseDirOfPdf2Html(rid) + "1.page");
			if (!firstPageFile.isFile()) {
				logger.error("convertPdf2Html(" + rid
						+ ") error: 预览失败，该pdf文档无法生成目标html(page1)文件或该文件已损坏！");
				throw new DocServiceException(
						"预览失败，该pdf文档无法生成目标html(page1)文件或该文件已损坏！");
			}
			
			// get page content
			File curPageFile = new File(rcUtil.getParseDirOfPdf2Html(rid) + start + ".page");
			if (!curPageFile.isFile()) {
				// The last page.
				return new PageVo<PdfVo>(null, 0);
			}
			List<String> pages = new ArrayList<String>();
			limit = limit >= 0 ? limit : 0;
			limit = limit == 0 ? Integer.MAX_VALUE : limit;
			
			File[] pageFiles = new File(rcUtil.getParseDirOfPdf2Html(rid)).listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (null != name && name.matches("\\d+\\.page")) {
						return true;
					} else {
						return false;
					}
				}
			});
			
			int totalPageCount = pageFiles.length;
			for (int i = 0; i < limit; i++) {
				curPageFile = new File(rcUtil.getParseDirOfPdf2Html(rid) + (start + i) + ".page");
				if (curPageFile.isFile()) {
					String curPageString = FileUtils.readFileToString(curPageFile, "UTF-8");
					curPageString = processImageUrlOfPdf(rcUtil.getParseUrlDir(rid) + "html/", curPageString);
					pages.add(curPageString);
				} else {
					break;
				}
			}

			List<PdfVo> data = new ArrayList<PdfVo>();
			// construct vo
			for (int i = 0; i < pages.size(); i++) {
				PdfVo pdf = new PdfVo();
				pdf.setContent(pages.get(i));
				data.add(pdf);
			}
			PageVo<PdfVo> page = new PageVo<PdfVo>(data, totalPageCount);
			page.setStyleUrl(rcUtil.getParseUrlDir(rid) + "html/" + RcUtil.getFileNameWithoutExt(rid) + ".css");
			page.setUrl(rcUtil.getParseUrlDir(rid) + "html/index.html");
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

	@Override
	public PageVo<AudioVo> convertAudio2Mp3(String rid) throws DocServiceException {
		try {
			convert(rid);
			String destPath = rcUtil.getParseDir(rid) + "index.mp3";
			File destFile = new File(destPath);
			if (!destFile.isFile()) {
				throw new DocServiceException("预览失败，未找到目标文件！");
			}

			List<AudioVo> data = new ArrayList<AudioVo>();
			AudioVo audioVo = new AudioVo();
			audioVo.setUrl(rcUtil.getParseUrlDir(rid) + "index.mp3");
			data.add(audioVo);
			PageVo<AudioVo> page = new PageVo<AudioVo>(data, 1);
			return page;
		} catch (Exception e) {
			logger.error("convertAudio2Mp3 error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}
	
	@Override
	public PageVo<ZipVo> convertZip2File(String rid) throws DocServiceException {
		try {
			convert(rid);
			File destDirFile = new File(rcUtil.getParseDir(rid));
			File[] extractedFiles = destDirFile.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isFile();
				}
			});

			if (extractedFiles.length < 1) {
				throw new DocServiceException("没有可预览的文件！");
			}

			List<ZipVo> data = new ArrayList<ZipVo>();
			for (File extractedFile : extractedFiles) {
				ZipVo zipVo = new ZipVo();
				String extractedFileName = extractedFile.getName();
				zipVo.setTitle(extractedFile.getName());
				boolean isViewable = ViewType.isViewableByFileName(extractedFileName);
				zipVo.setViewable(isViewable);
				zipVo.setPath(extractedFile.getAbsolutePath());
				data.add(zipVo);
			}
			PageVo<ZipVo> page = new PageVo<ZipVo>(data, data.size());
			return page;
		} catch (Exception e) {
			logger.error("convertZip2File error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<CadVo> convertDwg2Img(String rid) throws DocServiceException {
		try {
			convert(rid);
			File destDirFile = new File(rcUtil.getParseDir(rid));

			File[] pngFiles = destDirFile.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return null != name && name.toLowerCase().endsWith(".png");
				}
			});

			if (pngFiles.length < 1) {
				throw new DocServiceException("没有可预览的文件！");
			}

			List<CadVo> data = new ArrayList<CadVo>();
			for (File pngFile : pngFiles) {
				CadVo dwgVo = new CadVo();
				dwgVo.setUrl(rcUtil.getParseUrlDir(rid) + pngFile.getName());
				data.add(dwgVo);
			}
			PageVo<CadVo> page = new PageVo<CadVo>(data, data.size());
			return page;
		} catch (Exception e) {
			logger.error("convertDwg2Img error: " + e.getMessage());
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
				Date nowBefore5Min = DateUtils.addMinutes(new Date(), -5);
				if (utime.before(nowBefore5Min)) {
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
			if (ViewType.WORD == ViewType.getViewTypeByExt(ext)) {
				// view type 1: view by HTML
				if (viewPageStyleWord.contains("html")) {
					if (!destFile.isFile()) {
						convertResult = CmdUtil
								.runWindows(word2Html, src, dest);
					}
					if (!destFile.isFile()) {
						logger.error("[CONVERT ERROR] " + rid + " - "
								+ convertResult);
						throw new DocServiceException("对不起，该文档（"
								+ RcUtil.getUuidByRid(rid)
								+ "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
					}
				}
				// view type 1: view by image
				if (viewPageStyleWord.contains("img")
						|| viewPageStyleWord.contains("pdf")) {
					dest = rcUtil.getParseDir(rid) + RcUtil.getFileNameWithoutExt(rid) + ".pdf";
					destFile = new File(dest);
					
					// two steps to convert WORD to PNG
					// step 1. convert WORD to PDF
					convertResult = CmdUtil.runWindows(word2Pdf, "-src", src, "-dest", dest);
					if (!destFile.isFile()) {
						convertResult += CmdUtil.runWindows(word2Pdf, "-src", src, "-dest", dest);
						if (!destFile.isFile()) {
							logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
							throw new DocServiceException("对不起，该文档（"
									+ RcUtil.getUuidByRid(rid)
									+ "）暂无法预览，详情请联系管理员！");
						}
					}
					
					// step 2. convert PDF to PNG pictures
					String pngDestDir = rcUtil.getParseDirOfPdf2Png(rid);	// Directory MUST exist(Apache PDFBox)
					String pngDestFirstPage = pngDestDir + "1." + PDF_TO_IMAGE_TYPE;
					if (!new File(pngDestFirstPage).isFile()) {
						// String convertInfo = CmdUtil.runWindows("java", "-jar", pdf2img, "PDFToImage", "-imageType", PDF_TO_IMAGE_TYPE, "-outputPrefix", destDir, src);
						// convertResult += CmdUtil.runWindows(pdf2img, "-q", "-dNOPAUSE", "-dBATCH", "-sDEVICE=png16m", "-sPAPERSIZE=a3", "-dPDFFitPage", "-dUseCropBox", "-sOutputFile=" + pngDestDir + "%d.png", dest);
						convertResult += convertPdf2Img(pdf2img, pngDestDir, dest);
					}
					if (!new File(pngDestFirstPage).isFile()) {
						logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
						throw new DocServiceException("对不起，该文档（"
								+ RcUtil.getUuidByRid(rid) + "）暂无法预览，详情请联系管理员！");
					}
				}
			} else if (ViewType.EXCEL == ViewType.getViewTypeByExt(ext)) {
				// view type 1: view by HTML
				if (viewPageStyleExcel.contains("html")) {
					if (!destFile.isFile()) {
						convertResult = CmdUtil.runWindows(excel2Html, src,
								dest);
					}
					if (!destFile.isFile()) {
						logger.error("[CONVERT ERROR] " + rid + " - "
								+ convertResult);
						throw new DocServiceException("对不起，该文档（"
								+ RcUtil.getUuidByRid(rid)
								+ "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
					}
				}
				// view type 2: view by image
				if (viewPageStyleExcel.contains("img")
						|| viewPageStyleExcel.contains("pdf")) {
					dest = rcUtil.getParseDir(rid) + RcUtil.getFileNameWithoutExt(rid) + ".pdf";
					destFile = new File(dest);
					
					// two steps to convert EXCEL to PNG
					// step 1. convert EXCEL to PDF
					convertResult = CmdUtil.runWindows(excel2Pdf, src, dest);
					if (!destFile.isFile()) {
						convertResult += CmdUtil.runWindows(excel2Pdf, src, dest);
						if (!destFile.isFile()) {
							logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
							throw new DocServiceException("对不起，该文档（"
									+ RcUtil.getUuidByRid(uuid)
									+ "）暂无法预览，详情请联系管理员！");
						}
					}
					
					// step 2. convert PDF to PNG pictures
					String pngDestDir = rcUtil.getParseDirOfPdf2Png(rid);	// Directory MUST exist(Apache PDFBox)
					String pngDestFirstPage = pngDestDir + "1." + PDF_TO_IMAGE_TYPE;
					if (!new File(pngDestFirstPage).isFile()) {
						// String convertInfo = CmdUtil.runWindows("java", "-jar", pdf2img, "PDFToImage", "-imageType", PDF_TO_IMAGE_TYPE, "-outputPrefix", destDir, src);
						// convertResult += CmdUtil.runWindows(pdf2img, "-q", "-dNOPAUSE", "-dBATCH", "-sDEVICE=png16m", "-sPAPERSIZE=a3", "-dPDFFitPage", "-dUseCropBox", "-sOutputFile=" + pngDestDir + "%d.png", dest);
						convertResult += convertPdf2Img(pdf2img, pngDestDir, dest);
					}
					if (!new File(pngDestFirstPage).isFile()) {
						logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
						throw new DocServiceException("对不起，该文档（"
								+ RcUtil.getUuidByRid(rid) + "）暂无法预览，详情请联系管理员！");
					}
				}
			} else if (ViewType.PPT == ViewType.getViewTypeByExt(ext)) {
				dest = rcUtil.getParseDir(rid);
				destFile = new File(dest);
				if (ArrayUtils.isEmpty(new File(dest + IMG_WIDTH_200).listFiles())) {
					convertResult += CmdUtil.runWindows(ppt2Jpg, src, dest, "true", IMG_WIDTH_200);
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
			} else if (ViewType.PDF == ViewType.getViewTypeByExt(ext)) {
				// view type 1: view by HTML
				if (viewPageStylePdf.contains("html")) {
					// pdf2htmlEx
					String destDir = rcUtil.getParseDirOfPdf2Html(rid);
					destDir = destDir.replaceAll("/", "\\\\");
					destDir = destDir.endsWith("\\") ? destDir.substring(0, destDir.length() - 1) : destDir;
					String destIndex = destDir + "index.html";
					File destIndexFile = new File(destIndex);
					if (!destIndexFile.isFile()) {
						// option fallback(--fallback 1) is too slow, so do NOT use it.
						convertResult += CmdUtil.runWindows(pdf2html, "--embed", "cfijo", "--split-pages", "1", "--page-filename", "%d.page", "--zoom", "1.3", "--dest-dir", destDir, src.replaceAll("\\\\", "/"), "index.html");
					}
				}
				
				// view type 2: view by image
				if (viewPageStylePdf.contains("img")
						|| viewPageStylePdf.contains("pdf")) {
					String destDir = rcUtil.getParseDirOfPdf2Png(rid);	// Directory MUST exist(Apache PDFBox)
					String destFirstPage = destDir + "1." + PDF_TO_IMAGE_TYPE;
					// pdf2img, "-q", "-dSAFER", "-dBATCH", "-dNOPAUSE", "-r150", "-sDEVICE=png16m", "-dTextAlphaBits=4", "-dGraphicsAlphaBits=4", "-sOutputFile=" + destDir + "%d.png", src
					if (!new File(destFirstPage).isFile()) {
						// String convertInfo = CmdUtil.runWindows("java", "-jar", pdf2img, "PDFToImage", "-imageType", PDF_TO_IMAGE_TYPE, "-outputPrefix", destDir, src);
						// old style(before 20150624): convertResult += CmdUtil.runWindows(pdf2img, "-q", "-dNOPAUSE", "-dBATCH", "-sDEVICE=png16m", "-sPAPERSIZE=a3", "-dPDFFitPage", "-dUseCropBox", "-sOutputFile=" + destDir + "%d.png", src);
						convertResult += convertPdf2Img(pdf2img, destDir, src);
					}
					if (!new File(destFirstPage).isFile()) {
						logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
						throw new DocServiceException("对不起，该文档（"
								+ RcUtil.getUuidByRid(rid)
								+ "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
					}
				}
			} else if (ViewType.TXT == ViewType.getViewTypeByExt(ext)) {
				// do nothing.
			} else if (ViewType.IMG == ViewType.getViewTypeByExt(ext)) {
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
			} else if (ViewType.AUDIO == ViewType.getViewTypeByExt(ext)) {
				dest = rcUtil.getParseDir(rid);
				String destName = "index.mp3";
				if ("mp3".equalsIgnoreCase(ext)) {
					FileUtils.copyFile(srcFile, new File(dest, destName));
				} else {
					// Convert audio using FFMPEG
					String destPath = dest + destName;
					destFile = new File(destPath);
					if (!destFile.isFile()) {
						convertResult = CmdUtil.runWindows(audio2mp3, "-i", src, destPath);
					}
					if (!destFile.isFile()) {
						logger.error("[CONVERT ERROR] " + rid + " - "
								+ convertResult);
						throw new DocServiceException("对不起，该音频文件（"
								+ RcUtil.getUuidByRid(rid) + "）暂无法预览，请确认能正常打开！");
					}
				}
			} else if (ViewType.ZIP == ViewType.getViewTypeByExt(ext)) {
				File destDir = new File(rcUtil.getParseDir(rid));
				File[] extractedFiles = destDir.listFiles();
				if (extractedFiles.length < 1) {
					convertResult += CmdUtil.runWindows(zip2file, "e", src, "-o" + destDir, "-r", "-y");
					extractedFiles = destDir.listFiles();
				}
				if (extractedFiles.length < 1) {
					logger.error("[CONVERT ERROR] " + rid + " - " + convertResult);
					throw new DocServiceException("对不起，该压缩文件（"
							+ RcUtil.getUuidByRid(rid)
							+ "）暂无法预览，请确认能正常打开且包含文件！");
				}
			} else if (ViewType.CAD == ViewType.getViewTypeByExt(ext)) {
				dest = rcUtil.getParseDir(rid);
				String destName = "index.png";
				String destPath = dest + destName;
				File destDir = new File(dest);
				if (ArrayUtils.isEmpty(destDir.listFiles())) {
					src = src.replaceAll("/", "\\\\");
					destPath = destPath.replaceAll("/", "\\\\");
					convertResult = CmdUtil.runWindows(cad2img, "/Overwrite", "/OutLayout", "All", "/Hide", "/InFile", src, "/OutFile", destPath);
				}
				if (ArrayUtils.isEmpty(destDir.listFiles())) {
					logger.error("[CONVERT ERROR] " + rid + " - "
							+ convertResult);
					throw new DocServiceException("对不起，该CAD文件（"
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
	 * 				<li>Excel sheet file, use: rcUtil.getParseUrlDir(rid) + "index.files/" or "index_files/"</li>
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

	/**
	 * Convert pdf 2 png
	 * 
	 * @param pdf2img
	 * @param destDir dest dir, MUST end with slash/
	 * @param src source PDF file
	 * @return
	 */
	public static String convertPdf2Img(String pdf2img, String destDir, String src) {
		return CmdUtil.runWindows(pdf2img, "-q", "-dSAFER", "-dBATCH", "-dNOPAUSE", "-r150", "-sDEVICE=png16m", "-dTextAlphaBits=4", "-dGraphicsAlphaBits=4", "-sOutputFile=" + destDir + "%d.png", src);
	}
	
	public static String getEncoding(File file) {
		if (!file.isFile()) {
			return "UTF-8";
		}
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