package com.idocv.docview.service.impl;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.TextService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.RcUtil;
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

	private @Value("${converter.pdfsign}")
	String pdfSign;

	private @Value("${view.page.word.style}")
	String pageWordStyle;
	
	private @Value("${view.page.excel.style}")
	String pageExcelStyle;
	
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
			logger.error("convertWord2Html error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}
}