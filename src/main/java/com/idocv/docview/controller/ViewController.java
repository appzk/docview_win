package com.idocv.docview.controller;

import java.io.Serializable;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.PreviewService;
import com.idocv.docview.service.SessionService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;
import com.idocv.docview.vo.OfficeBaseVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.SessionVo;

@Controller
@RequestMapping("view")
public class ViewController {
	
	private static final Logger logger = LoggerFactory.getLogger(ViewController.class);
	
	@Resource
	private DocService docService;

	@Resource
	private PreviewService previewService;

	@Resource
	private SessionService sessionService;

	@RequestMapping("")
	public void home(HttpServletRequest req, HttpServletResponse resp) {
		String timeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println("VIEW HOME from " + req.getRemoteAddr() + " on " + timeString);
		resp.setStatus(301);
		resp.setHeader("Location", "http://www.idocv.com");
		resp.setHeader("Connection", "close");
		// return "redirect:http://www.idocv.com";
	}

	@RequestMapping("{id}")
	public String page(@RequestParam(defaultValue = "default") String template,
			@PathVariable String id,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size) {
		String lowerId = id.toLowerCase();
		String uuid = null;
		String sessionId = null;
		try {
			if (lowerId.matches("\\w{24}")) {
				// session id
				SessionVo sessionVo = sessionService.get(lowerId);
				if (null == sessionVo) {
					throw new DocServiceException("NOT a valid session!");
				}
				uuid = sessionVo.getUuid();
				sessionId = sessionVo.getId();
			} else {
				uuid = id;
			}
			if (uuid.endsWith("w")) {
				return "redirect:/page/word/index.html?uuid=" + uuid + (null == sessionId ? "" : "&session=" + sessionId);
			} else if (uuid.endsWith("x")) {
				return "redirect:/page/excel/index.html?uuid=" + uuid + (null == sessionId ? "" : "&session=" + sessionId);
			} else if (uuid.endsWith("p")) {
				return "redirect:/page/ppt/index.html?uuid=" + uuid + (null == sessionId ? "" : "&session=" + sessionId);
			} else if (uuid.endsWith("t")) {
				return "redirect:/page/txt/index.html?uuid=" + uuid + (null == sessionId ? "" : "&session=" + sessionId);
			} else {
				throw new DocServiceException(id + "is NOT a valid UUID!");
			}
		} catch (DocServiceException e) {
			logger.error("view error: ", e);
			return "redirect:/404.html";
		}
	}

	/**
	 * get document content by uuid in json format
	 * 
	 *  1. get docVo by uuid
	 *	2. check access mode of docVo
	 *	3. public mode -> direct view
	 *	4. semi-public | private mode -> 5
	 *	5. get sessionVo by sessionId
	 *	6. current time - ctime > expire time ? session expired : view.
	 *			PageVo<? extends Serializable> page = null;
	 * 
	 */
	@RequestMapping("{uuid}.json")
	@ResponseBody
	public PageVo<? extends Serializable> jsonUuid(
			@RequestParam(defaultValue = "default") String template,
			@PathVariable String uuid,
			@RequestParam(required = false) String session,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size) {
		PageVo<? extends Serializable> page = null;
		try {
			// 1. get docVo by uuid
			DocVo docVo = docService.getByUuid(uuid);
			if (null == docVo || StringUtils.isBlank(docVo.getRid())) {
				throw new DocServiceException("Document(" + uuid + ") NOT found!");
			}
			String rid = docVo.getRid();
			String ext = RcUtil.getExt(rid);

			// 2. check access mode of docVo
			int accessMode = docVo.getMode();
			if (0 == accessMode) {
				if (StringUtils.isBlank(session)) {
					throw new DocServiceException("This is NOT a public document, please provide a session id.");
				}
				// 5. get sessionVo by sessionId
				SessionVo sessionVo = sessionService.get(session);
				if (null == sessionVo) {
					throw new DocServiceException("Session NOT found!");
				}
				// 6. current time - ctime > expire time ? session expired :
				// view.
				long sessionCtime = sessionVo.getCtime();
				long currentTime = System.currentTimeMillis();
				if (currentTime - sessionCtime > 3600000) {
					throw new DocServiceException("Session expired, please get a new one!");
				}
				if (!uuid.equals(sessionVo.getUuid())) {
					throw new DocServiceException("Session is NOT consistent with UUID.");
				}
			}
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)
					|| "odt".equalsIgnoreCase(ext)) {
				page = previewService.convertWord2Html(rid, start, size);
			} else if ("xls".equalsIgnoreCase(ext)
					|| "xlsx".equalsIgnoreCase(ext)
					|| "ods".equalsIgnoreCase(ext)) {
				page = previewService.convertExcel2Html(rid, start, size);
			} else if ("ppt".equalsIgnoreCase(ext)
					|| "pptx".equalsIgnoreCase(ext)
					|| "odp".equalsIgnoreCase(ext)) {
				page = previewService.convertPPT2Html(rid, start, size);
			} else if ("txt".equalsIgnoreCase(ext)) {
				page = previewService.convertTxt2Html(rid);
			} else if ("pdf".equalsIgnoreCase(ext)) {
				// TODO
				String url = previewService.convertPdf2Swf(rid);
			} else {
				page = new PageVo<OfficeBaseVo>(null, 0);
				page.setCode(0);
				page.setDesc("Error: not a document type.");
			}
			page.setName(docVo.getName());
			page.setRid(docVo.getRid());
			page.setUuid(docVo.getUuid());
			docService.logView(uuid);
		} catch (Exception e) {
			logger.error("jsonUuid error: ", e);
			page = new PageVo<OfficeBaseVo>(null, 0);
			page.setCode(0);
			page.setDesc(e.getMessage());
			page.setUuid(uuid);
		}
		return page;
	}

	@RequestMapping("url")
	public String previewUrl(HttpServletRequest req,
			@RequestParam(required = true) String url,
			@RequestParam(required = false) String name,
			@RequestParam(value = "mode", defaultValue = "public") String modeString) {
		try {
			int mode = 0;
			if ("public".equalsIgnoreCase(modeString)) {
				mode = 1;
			}
			url = URLDecoder.decode(url, "UTF-8");
			if (StringUtils.isBlank(name) && url.contains(".") && url.matches(".*/[^/]+\\.[^/]+")) {
				name = url.replaceFirst(".*/([^/]+\\.[^/]+)", "$1");
			}
			String ip = req.getRemoteAddr();
			String appKey = "doctest";
			DocVo po = docService.addUrl(appKey, url, name, mode);
			if (null == po) {
				throw new DocServiceException("Upload URL document error!");
			}
			String uuid = po.getUuid();
			return "redirect:" + uuid;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
