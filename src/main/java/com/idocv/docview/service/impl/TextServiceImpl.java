package com.idocv.docview.service.impl;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.TextService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.ExcelVo;
import com.idocv.docview.vo.PPTVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.WordVo;

@Service
public class TextServiceImpl implements TextService {

	private static Logger logger = LoggerFactory.getLogger(TextServiceImpl.class);
	
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Resource
	private ViewService viewService;

	@Resource
	private RcUtil rcUtil;
	
	@Resource
	private DocDao docDao;

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

	@Override
	public PageVo<WordVo> getWordText(String rid, int start, int limit) throws DocServiceException{
		try {
			viewService.convertWord2Html(rid, 0, 1);
			
			// check text
			File textFile = new File(rcUtil.getParseDir(rid) + "text.txt");
			String textString;
			if (!textFile.isFile()) {
				File bodyFile = new File(rcUtil.getParseDir(rid) + "body.html");
				Document textDocument = Jsoup.parse(bodyFile, "UTF-8");
				textString = textDocument.text();
				FileUtils.writeStringToFile(textFile, textString, "UTF-8");
			} else {
				textString = FileUtils.readFileToString(textFile, "UTF-8");
			}

			if (null != textString) {
				textString = textString.replaceAll("[\\s 　]+", " ").trim();
			}

			List<WordVo> data = new ArrayList<WordVo>();
			// construct vo
			WordVo word = new WordVo();
			word.setText(textString);
			data.add(word);
			PageVo<WordVo> page = new PageVo<WordVo>(data, 1);
			try {
				List<String> titles = FileUtils.readLines(new File(rcUtil.getParseDir(rid) + "titles.txt"), "UTF-8");
				page.setTitles(titles);
			} catch (Exception e) {
				logger.warn("获取标题失败：" + e.getMessage());
			}
			return page;
		} catch (Exception e) {
			logger.error("getWordText error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}
	
	@Override
	public PageVo<ExcelVo> getExcelText(String rid, int start, int limit) throws DocServiceException {
		try {
			viewService.convertExcel2Html(rid, 0, 1);
			
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
			for (File excelFile : excelFiles) {
				if (excelFile.getName().matches("sheet\\d+\\.html")) {
					sheetFiles.add(excelFile);
				} else if (excelFile.getName().equalsIgnoreCase("tabstrip.html")) {
					tabstripFile = excelFile;
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

				// get content
				// String sheetContent = sheetFileContent.replaceFirst("(?s)(?i).+?<body.+?(<table[^>]+)(>.*</table>).*(?-i)", "$1" + " class=\"table table-condensed table-bordered\"" + "$2");
				Document sheetDocument = Jsoup.parse(sheetFiles.get(i), "UTF-8");
				String sheetContent = sheetDocument.text();
				vo.setTitle(title);
				vo.setText(sheetContent);
				VoList.add(vo);
			}
			PageVo<ExcelVo> page = new PageVo<ExcelVo>(VoList, sheetFiles.size());
			return page;
		} catch (Exception e) {
			logger.error("getExcelText error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}
	
	@Override
	public PageVo<PPTVo> getPPTText(String rid, int start, int limit) throws DocServiceException {
		try {
			return viewService.convertPPT2Img(rid, 0, 1);
		} catch (Exception e) {
			logger.error("getPPTText(" + rid + ") error: ", e.fillInStackTrace());
			throw new DocServiceException(e.getMessage(), e);
		}
	}
}