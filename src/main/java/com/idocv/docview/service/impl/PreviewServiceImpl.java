package com.idocv.docview.service.impl;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.PreviewService;
import com.idocv.docview.util.CmdUtil;
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
	
	@Resource
	private DocDao docDao;

	private @Value("${office.cmd.word2html}")
	String word2Html;
	private static int WORD_PAGING_LINE_COUNT = 200;
	private static int WORD_PAGING_CHAR_COUNT = 3000;

	private @Value("${office.cmd.excel2html}")
	String excel2Html;

	private @Value("${office.cmd.ppt2jpg}")
	String ppt2Jpg;
	
	private @Value("${swftools.cmd.pdf2swf}")
	String pdf2swf;

	private static final String encodingString = "(?s)(?i).*?<meta[^>]+?http-equiv=[^>]+?charset=([^\"^>]+?)\"?>.*";

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
					System.out.println("length: " + lines.length);
				}
			}

			// modify picture path from RELATIVE to ABSOLUTE url.
			// bodyString = processImageUrl(rcUtil.getParseUrlDir(rid), bodyString);
			
			// get page content
			File curPageFile = new File(rcUtil.getParseDir(rid) + start + ".html");
			if (!curPageFile.isFile()) {
				throw new DocServiceException("已是最后一页！");
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
			logger.error("convertWord2Html error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<ExcelVo> convertExcel2Html(String rid, int start, int limit) throws DocServiceException {
		try {
			convert(rid);
			File rawFilesDir = new File(rcUtil.getParseDir(rid) + "index.files");
			if (!rawFilesDir.isDirectory()) {
				throw new DocServiceException("Can't find parsed directory!");
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
				throw new Exception("Excel parsed files NOT found!");
			}
			
			// get TITLE(s) and CONTENT(s)
			List<ExcelVo> VoList = new ArrayList<ExcelVo>();
			String titleFileContent = FileUtils.readFileToString(tabstripFile, "GBK");
			System.err.println("Processing excel file - " + rid);
			for (int i = 0; i < sheetFiles.size(); i++) {
				ExcelVo vo = new ExcelVo();

				// get title
				String title = titleFileContent.replaceFirst("(?s)(?i).+?" + sheetFiles.get(i).getName() + ".+?<font[^>]+>(.+?)</font>.*(?-i)", "$1");
				title = StringUtils.isBlank(title) ? ("Sheet" + (i + 1)) : title;
				// titleList.add(title);
				System.err.println("    title" + (i + 1) + " = " + title);

				// get content
				String sheetFileContent = FileUtils.readFileToString(sheetFiles.get(i), "GBK");
				String sheetContent = sheetFileContent.replaceFirst("(?s)(?i).+?<body.+?(<table[^>]+)(>.*</table>).*(?-i)", "$1" + " class=\"table table-condensed table-bordered\"" + "$2");
				sheetContent = sheetContent.replaceFirst("table-layout:fixed;", "");
				sheetContent = processImageUrl(rcUtil.getParseUrlDir(rid) + "index.files/", sheetContent);
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
			logger.error("convertExcel2Html error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public PageVo<PPTVo> convertPPT2Html(String rid, int start, int limit) throws DocServiceException {
		try {
			// get page count
			File[] slide960Files = new File(rcUtil.getParseDirOfPPT960x720(rid)).listFiles();
			File[] slide200Files = new File(rcUtil.getParseDirOfPPT200x150(rid)).listFiles();
			if (slide960Files.length <= 0 || slide200Files.length <= 0) {
				convert(rid);
				slide960Files = new File(rcUtil.getParseDirOfPPT960x720(rid)).listFiles();
				slide200Files = new File(rcUtil.getParseDirOfPPT200x150(rid)).listFiles();
			}

			// sort file
			Arrays.sort(slide960Files, new PPT2JPGComparator());
			Arrays.sort(slide200Files, new PPT2JPGComparator());

			List<PPTVo> data = new ArrayList<PPTVo>();
			if (slide960Files.length > 0 && slide200Files.length > 0) {
				for (int i = 0; i < slide960Files.length && i < slide200Files.length; i++) {
					PPTVo ppt = new PPTVo();
					String url = rcUtil.getParseUrlDir(rid) + "960x720/" + slide960Files[i].getName();
					String thumbUrl = rcUtil.getParseUrlDir(rid) + "200x150/" + slide200Files[i].getName();
					ppt.setUrl(url);
					ppt.setThumbUrl(thumbUrl);
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
	public String convertPdf2Swf(String rid) throws DocServiceException {
		try {
			String src = rcUtil.getPath(rid);
			String dest = rcUtil.getParseDir(rid) + RcUtil.getFileNameWithoutExt(rid) + ".swf";
			File destFile = new File(dest);
			if (!destFile.isFile()) {
				String convertInfo = CmdUtil.runWindows(pdf2swf + " " + src + " -o " + dest + " -f -T 9 -t -s storeallcharacters -s languagedir=C:\\xpdf\\xpdf-chinese-simplified ");
				System.out.println("Convert info: \n" + convertInfo);
				if (!destFile.isFile()) {
					System.out.println("Can't convert \"" + src + "\" to \"" + dest + "\", start converting using poly2bitmap parameter...");
					convertInfo = CmdUtil.runWindows(pdf2swf + " " + src + " -o " + dest + " -f -T 9 -t -s storeallcharacters -s poly2bitmap -s languagedir=C:\\xpdf\\xpdf-chinese-simplified ");
					System.out.println("Convert info 2: \n" + convertInfo);
				}
			}
			return rcUtil.getParseUrlDir(rid) + RcUtil.getFileNameWithoutExt(rid) + ".swf";
		} catch (Exception e) {
			logger.error("convertPdf2Swf error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	public boolean convert(String rid) throws DocServiceException {
		return convert(rid, 0);
	}

	private boolean convert(String rid, int tryCount) throws DocServiceException {
		RcUtil.validateRid(rid);
		String src = rcUtil.getPath(rid);
		File srcFile = new File(src);
		String dest = rcUtil.getParsePathOfHtml(rid);
		File destFile = new File(dest);
		if (!srcFile.isFile()) {
			logger.error("文件未找到, rid=" + rid);
			throw new DocServiceException(404, "文件未找到");
		}
		String ext = RcUtil.getExt(rid);
		long size = RcUtil.getSizeByRid(rid);
		long startTime = 0;
		if (convertingRids.contains(rid)) {
			if (tryCount < 10) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				convert(rid, ++tryCount);
			} else {
				if (size > 1000000) {
					throw new DocServiceException("您的文档较大，正在努力处理中，请稍后再试！");
				} else {
					throw new DocServiceException("您的文档正在处理中，请稍后再试！");
				}
			}
		} else {
			System.err.println("convertingRids(u+) " + rid);
			startTime = System.currentTimeMillis();
			convertingRids.add(rid);
		}
		try {
			String convertResult = null;
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)) {
				if (!destFile.isFile()) {
					convertResult = CmdUtil.runWindows(word2Html, src, dest);
				}
				if (!destFile.isFile()) {
					logger.error("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
					throw new DocServiceException("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
				}
			} else if ("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext)) {
				if (!destFile.isFile()) {
					convertResult = CmdUtil.runWindows(excel2Html, src, dest);
				}
				if (!destFile.isFile()) {
					logger.error("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
					throw new DocServiceException("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
				}
			} else if ("ppt".equalsIgnoreCase(ext) || "pptx".equalsIgnoreCase(ext)) {
				dest = rcUtil.getParseDir(rid);
				File dir960 = new File(rcUtil.getParseDirOfPPT960x720(rid));
				File dir200 = new File(rcUtil.getParseDirOfPPT200x150(rid));
				destFile = new File(dest);
				if (dir960.listFiles().length <= 0 || dir200.listFiles().length <= 0) {
					// convertResult = CmdUtil.runWindows(ppt2Jpg, src, destFile.getAbsolutePath(), "save");
					convertResult = CmdUtil.runWindows(ppt2Jpg, src, dir960.getAbsolutePath() + File.separator, "export", "960", "720");
					convertResult += CmdUtil.runWindows(ppt2Jpg, src, dir200.getAbsolutePath() + File.separator, "export", "200", "150");
				}
				if (dir960.listFiles().length <= 0 || dir200.listFiles().length <= 0) {
					logger.error("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
					throw new DocServiceException("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或已损坏，请确认能正常打开！");
				}
			} else if ("pdf".equalsIgnoreCase(ext)) {
				dest = rcUtil.getParseDir(rid) + RcUtil.getFileNameWithoutExt(rid) + ".swf";
				destFile = new File(dest);
				if (!destFile.isFile()) {
					String convertInfo = CmdUtil.runWindows(pdf2swf + " " + src + " -o " + dest + " -f -T 9 -t -s storeallcharacters -s languagedir=C:\\xpdf\\xpdf-chinese-simplified ");
					System.out.println("Convert info: \n" + convertInfo);
					if (!destFile.isFile()) {
						System.out.println("Can't convert \"" + src + "\" to \"" + dest + "\", start converting using poly2bitmap parameter...");
						convertInfo = CmdUtil.runWindows(pdf2swf + " " + src + " -o " + dest + " -f -T 9 -t -s storeallcharacters -s poly2bitmap -s languagedir=C:\\xpdf\\xpdf-chinese-simplified ");
						System.out.println("Convert info 2: \n" + convertInfo);
					}
				}
			} else if ("txt".equalsIgnoreCase(ext)) {
				// do nothing.
			} else {
				throw new DocServiceException("目前不支持（" + ext + "）格式！");
			}
			System.err.println("Convert result: " + convertResult);
			return true;
		} catch (Exception e) {
			logger.error("convert error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		} finally {
			System.err.println("convertingRids(u-) " + rid);
			long endTime = System.currentTimeMillis();
			convertingRids.remove(rid);
			if (startTime > 0) {
				System.err.println("Convert " + rid + " with size " + size + " elapse: " + (endTime - startTime) + ", rate: " + (size / ((endTime - startTime) / 1000d)) + " bit/s.");
			}
		}
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

class PPT2JPGComparator implements Comparator<File> {
	
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