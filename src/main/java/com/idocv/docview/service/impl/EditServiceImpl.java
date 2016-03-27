package com.idocv.docview.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.EditService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.CmdUtil;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.WordVo;

@Service
public class EditServiceImpl implements EditService {
	
	private static final Logger logger = LoggerFactory.getLogger(EditServiceImpl.class);

	@Resource
	private RcUtil rcUtil;

	@Resource
	private DocService docService;

	@Resource
	private ViewService viewService;
	
	private @Value("${converter.html2word}")
	String html2word;

	@Override
	public void save(String uuid, String body) throws DocServiceException {
		try {
			int lastVersion = getLatestVersion(uuid);
			// file NOT exist
			if (lastVersion < 0) {
				return;
			}
			
			DocVo vo = docService.getByUuid(uuid);
			String rid = vo.getRid();
			File curVerDir = new File(rcUtil.getParseDir(rid) + "v" + (lastVersion + 1));
			if (!curVerDir.isDirectory()) {
				curVerDir.mkdirs();
			}
			FileUtils.writeStringToFile(new File(curVerDir, "body.html"), body, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get latest version number
	 * 
	 * @param uuid
	 * @return -1: file NOT eixt. 0: exist but NOT have a version. >0: real version number
	 * @throws DocServiceException
	 */
	@Override
	public int getLatestVersion(String uuid) throws DocServiceException {
		if (StringUtils.isBlank(uuid)) {
			return -1;
		}
		DocVo vo = docService.getByUuid(uuid);
		if (null == vo) {
			return -1;
		}
		String rid = vo.getRid();
		String versionRootDir = rcUtil.getParseDir(rid);
		String[] fs = new File(versionRootDir).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return (null != name && name.matches("v\\d+")) ? true : false;
			}
		});
		if (null == fs || fs.length < 1) {
			return 0;
		}
		return fs.length;
	}

	@Override
	public String getBody(String uuid, int version) throws DocServiceException {
		int latestVersion = getLatestVersion(uuid);
		// file NOT exist
		if (latestVersion < 0) {
			return "";
		}
		DocVo vo = docService.getByUuid(uuid);
		String rid = vo.getRid();

		version = (version > latestVersion) ? latestVersion : version;
		version = (version < 0) ? latestVersion : version;
		if (version == 0) { // original doc
			PageVo<WordVo> pageVo = viewService.convertWord2HtmlAll(rid);
			if (null != pageVo && org.apache.commons.collections.CollectionUtils.isNotEmpty(pageVo.getData())) {
				return pageVo.getData().get(0).getContent();
			}
		}

		File latestVerDir = new File(rcUtil.getParseDir(rid) + "v" + version);
		File versionFie = new File(latestVerDir + File.separator + "body.html");
		if (!versionFie.isFile()) {
			return "";
		}
		try {
			String content = FileUtils.readFileToString(versionFie, "UTF-8");
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public String getDocPathByVersion(String uuid, int version) throws DocServiceException {
		int latestVersion = getLatestVersion(uuid);
		// file NOT exist
		if (latestVersion < 0) {
			return "";
		}
		DocVo vo = docService.getByUuid(uuid);
		String rid = vo.getRid();

		version = (version > latestVersion) ? latestVersion : version;
		version = (version < 0) ? latestVersion : version;
		if (version == 0) { // original doc
			return rcUtil.getPath(rid);
		}
		File baseDir = new File(rcUtil.getParseDir(rid));
		File versionDir = new File(rcUtil.getParseDir(rid) + "v" + version);
		File versionBodyFie = new File(versionDir + File.separator + "body.html");
		File versionDocxFie = new File(versionDir + File.separator + "body.docx");
		if (!versionDocxFie.isFile()) {
			String result = CmdUtil.runWindows("cd /D", baseDir.getAbsolutePath(), "&", html2word, versionBodyFie.getAbsolutePath(), "-o", versionDocxFie.getAbsolutePath());
			logger.info("[EDIT WORD] convert body html to docx when download docx: " + result);
		}
		if (!versionDocxFie.isFile()) {
			logger.info("生成编辑后文档失败，uuid=" + uuid + ", version=" + version);
			throw new DocServiceException("生成编辑后文档失败");
		}
		return versionDocxFie.getAbsolutePath();
	}

	@Override
	public String getHtmlBody(String uuid) throws DocServiceException {
		// 1. get docVo by uuid
		DocVo docVo = docService.getByUuid(uuid);
		if (null == docVo || StringUtils.isBlank(docVo.getRid())) {
			logger.error("Document(" + uuid + ") NOT found!");
			throw new DocServiceException("Document(" + uuid + ") NOT found!");
		}
		String rid = docVo.getRid();
		PageVo<WordVo> wordPage = viewService.convertWord2Html(rid, 1, 0);
		List<WordVo> pageData = wordPage.getData();
		if (CollectionUtils.isEmpty(pageData)) {
			logger.error("Document(" + uuid + ") content is empty!");
			throw new DocServiceException("Document(" + uuid + ") content is empty!");
		}
		StringBuilder sb = new StringBuilder();
		for (WordVo vo : pageData) {
			String content = vo.getContent();
			sb.append(content);
		}
		String cleanText = Jsoup.clean(sb.toString(), Whitelist.basic());
		return cleanText;
	}

}