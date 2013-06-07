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
			
			// read body
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

			String bodyString = bodyRaw;

			// modify picture path from RELATIVE to ABSOLUTE url.
			bodyString = processImageUrl(rcUtil.getParseUrlDir(rid), bodyString);
			bodyString = processStyle(bodyString);
			
			// paging
			List<String> pages = new ArrayList<String>();
			String pageRegex = "(?s)(?i)(.+?)(<span[^>]+?>[\\s]*<br[^>]*?style=[\"|\'][^>]*page-break-before[^>]+>[\\s]*</span>)(.*)(?-i)";
			while (bodyString.matches(pageRegex)) {
				String page = bodyString.replaceFirst(pageRegex, "$1");
				bodyString = bodyString.replaceFirst(pageRegex, "$3");
				pages.add(page);
			}
			pages.add(bodyString);
			
			List<WordVo> data = new ArrayList<WordVo>();
			// construct vo
			for (String page : pages) {
				WordVo word = new WordVo();
				word.setContent(page);
				data.add(word);
			}
			PageVo<WordVo> page = new PageVo<WordVo>(data, data.size());
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
			File[] slideFiles = new File(rcUtil.getParseDir(rid)).listFiles();
			if (slideFiles.length <= 0) {
				convert(rid);
				slideFiles = new File(rcUtil.getParseDir(rid)).listFiles();
			}

			// sort file
			Arrays.sort(slideFiles, new Comparator<File>() {

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
			});

			List<PPTVo> data = new ArrayList<PPTVo>();
			if (slideFiles.length > 0) {
				for (int i = 0; i < slideFiles.length; i++) {
					PPTVo ppt = new PPTVo();
					String url = rcUtil.getParseUrlDir(rid) + slideFiles[i].getName();
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
	public PageVo<TxtVo> convertTxt2Html(String rid) throws DocServiceException {
		try {
			File src = new File(rcUtil.getPath(rid));
			List<TxtVo> data = new ArrayList<TxtVo>();
			String content = FileUtils.readFileToString(src, getEncoding(src));
			// content = content.replaceAll("\n|\r\n|\r", "<br />");
			TxtVo vo = new TxtVo();
			vo.setContent(content);
			data.add(vo);
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
					logger.error("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或其它限制，请取消限制后重试！");
					throw new DocServiceException("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或其它限制，请取消限制后重试！");
				}
			} else if ("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext)) {
				if (!destFile.isFile()) {
					convertResult = CmdUtil.runWindows(excel2Html, src, dest);
				}
				if (!destFile.isFile()) {
					logger.error("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或其它限制，请取消限制后重试！");
					throw new DocServiceException("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或其它限制，请取消限制后重试！");
				}
			} else if ("ppt".equalsIgnoreCase(ext) || "pptx".equalsIgnoreCase(ext)) {
				dest = rcUtil.getParseDir(rid);
				destFile = new File(dest);
				if (destFile.listFiles().length <= 0) {
					convertResult = CmdUtil.runWindows(ppt2Jpg, src, destFile.getAbsolutePath(), "save");
				}
				if (destFile.listFiles().length <= 0) {
					logger.error("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或其它限制，请取消限制后重试！");
					throw new DocServiceException("对不起，该文档（" + RcUtil.getUuidByRid(rid) + "）暂无法预览，可能设置了密码或其它限制，请取消限制后重试！");
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
