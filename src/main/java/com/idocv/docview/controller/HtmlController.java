package com.idocv.docview.controller;

import com.idocv.docview.common.DocResponse;
import com.idocv.docview.util.CmdUtil;
import com.idocv.docview.util.MimeUtil;
import com.idocv.docview.util.RcUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Controller
@RequestMapping("html")
public class HtmlController {
	
	private static final Logger logger = LoggerFactory.getLogger(HtmlController.class);

	@Resource
	private RcUtil rcUtil;

	private @Value("${converter.html2word}")
	String html2word;

	private @Value("${converter.url2html}")
	String url2html;

	@RequestMapping("2word")
	@ResponseBody
	public void toWord(HttpServletRequest req,
					   HttpServletResponse resp,
					   @RequestParam(value = "url", required = true) String url) {
		try {
			File htmlDir = new File(rcUtil.getDataDir() + "urlhtml/");
			if (!htmlDir.isDirectory()) {
				htmlDir.mkdirs();
			}
			String encodedUrl = DigestUtils.md5Hex(url);
			// String encodedUrl = URLEncoder.encode(url, "UTF-8");
			File destHtmlFile = new File(htmlDir, encodedUrl + ".html");
			File destWordFile = new File(htmlDir, encodedUrl + ".docx");
			String result = "";
			if (!destHtmlFile.isFile()) {
				result = CmdUtil.runWindows("java", "-jar", url2html, url, destHtmlFile.getAbsolutePath());
			}
			if (!destWordFile.isFile()) {
				result += CmdUtil.runWindows("cd /D", htmlDir.getAbsolutePath(), "&", html2word, destHtmlFile.getAbsolutePath(), "-o", destWordFile.getAbsolutePath());
			}
			logger.info("url 2 word done! (url=" + url + ") result: " + result);

			String contentType = MimeUtil.getContentType("docx");
			if (StringUtils.isNotBlank(contentType)) {
				resp.setContentType(contentType);
			}
			DocResponse.setResponseHeaders(req, resp, encodedUrl + ".docx");
			IOUtils.write(FileUtils.readFileToByteArray(destWordFile), resp.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}