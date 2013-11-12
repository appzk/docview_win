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
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.AppService;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.SessionService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.IpUtil;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.AppVo;
import com.idocv.docview.vo.DocVo;
import com.idocv.docview.vo.OfficeBaseVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.SessionVo;

@Controller
@RequestMapping("view")
public class ViewController {
	
	private static final Logger logger = LoggerFactory.getLogger(ViewController.class);
	
	@Resource
	private AppService appService;

	@Resource
	private DocService docService;

	@Resource
	private ViewService previewService;

	@Resource
	private SessionService sessionService;

	@RequestMapping("")
	public void home(HttpServletRequest req, HttpServletResponse resp) {
		String timeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println("VIEW HOME from " + IpUtil.getIpAddr(req) + " on " + timeString);
		resp.setStatus(301);
		resp.setHeader("Location", "http://www.idocv.com");
		resp.setHeader("Connection", "close");
		// return "redirect:http://www.idocv.com";
	}

	@RequestMapping("{id}")
	public ModelAndView page(ModelAndView model,
			@RequestParam(defaultValue = "default") String style,
			@PathVariable String id,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size) {
		String uuid = null;
		String sessionId = null;
		try {
			if (id.matches("\\w{24}")) {
				// session id
				SessionVo sessionVo = sessionService.get(id);
				if (null == sessionVo) {
					throw new DocServiceException("无效的会话ID！");
				}
				uuid = sessionVo.getUuid();
				sessionId = sessionVo.getId();
			} else {
				uuid = id;
			}
			if (uuid.endsWith("w")) {
				model.setViewName("word/index");
				return model;
//				return "redirect:/page/word/index.html?uuid=" + uuid + (null == sessionId ? "" : "&session=" + sessionId);
			} else if (uuid.endsWith("x")) {
				model.setViewName("excel/index");
				return model;
//				return "redirect:/page/excel/index.html?uuid=" + uuid + (null == sessionId ? "" : "&session=" + sessionId);
			} else if (uuid.endsWith("p")) {
				if ("3d".equalsIgnoreCase(style)) {
					model.setViewName("ppt/index");
					return model;
				} else if ("carousel".equalsIgnoreCase(style)) {
					model.setViewName("ppt/carousel");
					return model;
				} else if ("test".equalsIgnoreCase(style)) {
					model.setViewName("ppt/test");
					return model;
				} else if ("speaker".equalsIgnoreCase(style)) {
					model.setViewName("ppt/sync-speaker");
					return model;
				} else if ("audience".equalsIgnoreCase(style)) {
					model.setViewName("ppt/sync-audience");
					return model;
				} else {
					model.setViewName("ppt/outline");
					return model;
				}
//				return "redirect:/page/ppt/index.html?uuid=" + uuid + (null == sessionId ? "" : "&session=" + sessionId);
			} else if (uuid.endsWith("f")) {
				// model.setViewName("pdf/index");
				model.setViewName("pdf/png");
				return model;
			} else if (uuid.endsWith("t")) {
				model.setViewName("txt/index");
				return model;
//				return "redirect:/page/txt/index.html?uuid=" + uuid + (null == sessionId ? "" : "&session=" + sessionId);
			} else {
				throw new DocServiceException("(" + id + ")不是有效的文档！");
			}
		} catch (DocServiceException e) {
			logger.error("view id(" + id + ") error: " + e.getMessage());
			model.setViewName("404");
			model.addObject("error", e.getMessage());
			return model;
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
	@RequestMapping("{id}.json")
	@ResponseBody
	public PageVo<? extends Serializable> jsonUuid(
			@RequestParam(defaultValue = "default") String template,
			@PathVariable String id,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size) {
		PageVo<? extends Serializable> page = null;
		String uuid = null;
		String rid = null;
		String session = null;
		try {
			if (id.matches("\\w{24}")) {
				// session id
				SessionVo sessionVo = sessionService.get(id);
				if (null == sessionVo) {
					throw new DocServiceException("会话不存在！");
				}
				uuid = sessionVo.getUuid();
				session = sessionVo.getId();
			} else {
				uuid = id;
			}

			// 1. get docVo by uuid
			DocVo docVo = docService.getByUuid(uuid);
			if (null == docVo || StringUtils.isBlank(docVo.getRid())) {
				throw new DocServiceException("文档(" + uuid + ")不存在！");
			}
			rid = docVo.getRid();
			String ext = RcUtil.getExt(rid);

			// 2. check access mode of docVo
			int accessMode = docVo.getStatus();
			if (0 == accessMode) {
				if (StringUtils.isBlank(session)) {
					throw new DocServiceException("私有文档不能公开访问，请使用会话id来访问！");
				}
				// 5. get sessionVo by sessionId
				SessionVo sessionVo = sessionService.get(session);
				if (null == sessionVo) {
					throw new DocServiceException("会话不存在！");
				}
				// 6. current time - ctime > expire time ? session expired :
				// view.
				String sessionCtimeString = sessionVo.getCtime();
				long sessionCtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionCtimeString).getTime();
				long currentTime = System.currentTimeMillis();
				if (currentTime - sessionCtime > 3600000) {
					throw new DocServiceException("会话已过期，请重新获取一个会话！");
				}
				if (!uuid.equals(sessionVo.getUuid())) {
					throw new DocServiceException("该会话和文档不一致，无法预览！");
				}
			}
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)
					|| "odt".equalsIgnoreCase(ext)) {
				start = (start - 1) * size + 1;
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
				start = (start - 1) * size + 1;
				page = previewService.convertTxt2Html(rid, start, size);
			} else if ("pdf".equalsIgnoreCase(ext)) {
				// page = previewService.convertPdf2Html(rid, 1, 0);
				page = previewService.convertPdf2Img(rid, 1, 0);
			} else {
				page = new PageVo<OfficeBaseVo>(null, 0);
				page.setCode(0);
				page.setDesc("不是一个文档！");
			}
			if (CollectionUtils.isEmpty(page.getData())) {
				page.setCode(0);
				page.setDesc("没有可显示的内容！");
			}
			page.setName(docVo.getName());
			page.setRid(docVo.getRid());
			page.setUuid(docVo.getUuid());
			docService.logView(uuid);
		} catch (Exception e) {
			logger.error("view id.json(" + id + ") error: " + e.getMessage());
			page = new PageVo<OfficeBaseVo>(null, 0);
			page.setCode(0);
			page.setDesc(e.getMessage());
			page.setUuid(uuid);
			page.setRid(rid);
		}
		return page;
	}

	@RequestMapping("{uuid}.html")
	public ModelAndView html(
			ModelAndView model,
			@PathVariable String uuid,
			@RequestParam(required = false) String session,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size) {
		PageVo<? extends Serializable> page = null;
		try {
			// 1. get docVo by uuid
			DocVo docVo = docService.getByUuid(uuid);
			if (null == docVo || StringUtils.isBlank(docVo.getRid())) {
				throw new DocServiceException("文件(" + uuid + ")未找到！");
			}
			String rid = docVo.getRid();
			String ext = RcUtil.getExt(rid);

			// 2. check access mode of docVo
			int accessMode = docVo.getStatus();
			if (0 == accessMode) {
				if (StringUtils.isBlank(session)) {
					throw new DocServiceException("这是一个私有文档，请用会话ID来访问！");
				}
				// 5. get sessionVo by sessionId
				SessionVo sessionVo = sessionService.get(session);
				if (null == sessionVo) {
					logger.error("不存在该会话！");
					throw new DocServiceException("不存在该会话！");
				}
				// 6. current time - ctime > expire time ? session expired :
				// view.
				String sessionCtimeString = sessionVo.getCtime();
				long sessionCtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionCtimeString).getTime();
				long currentTime = System.currentTimeMillis();
				if (currentTime - sessionCtime > 3600000) {
					logger.error("会话已过期，请重新获取一个新的会话！");
					throw new DocServiceException("会话已过期，请重新获取一个新的会话！");
				}
				if (!uuid.equals(sessionVo.getUuid())) {
					logger.error("该会话与稳定UUID不一致！" + ", session=" + session
							+ ", uuid=" + uuid);
					throw new DocServiceException("该会话与稳定UUID不一致！");
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
				page = previewService.convertTxt2Html(rid, start, size);
			} else if ("pdf".equalsIgnoreCase(ext)) {
				page = previewService.convertPdf2Html(rid, 1, 0);
			} else {
				page = new PageVo<OfficeBaseVo>(null, 0);
				page.setCode(0);
				logger.error("暂不支持该文件类型（" + ext + "）的预览！");
				page.setDesc("暂不支持该文件类型（" + ext + "）的预览！");
			}
			page.setName(docVo.getName());
			page.setRid(docVo.getRid());
			page.setUuid(docVo.getUuid());
			docService.logView(uuid);
		} catch (Exception e) {
			logger.error("jsonUuid error(" + uuid + "): ", e);
			page = new PageVo<OfficeBaseVo>(null, 0);
			page.setCode(0);
			page.setDesc(e.getMessage());
			page.setUuid(uuid);
		}
		model.addObject("page", page);
		if (uuid.endsWith("w")) {
			model.setViewName("word/static");
		} else if (uuid.endsWith("x")) {
			model.setViewName("excel/static");
		} else if (uuid.endsWith("p")) {
			model.setViewName("ppt/static");
		} else if (uuid.endsWith("t")) {
			model.setViewName("txt/static");
		} else {
			logger.error("view uuid.html(" + uuid + ") error: 未知文件格式！");
			model.setViewName("404");
		}
		return model;
	}
	
	@RequestMapping("url")
	public String viewUrl(
			HttpServletRequest req,
			Model model,
			@RequestParam(required = true) String url,
			@RequestParam(value = "token", defaultValue = "testtoken") String token,
			@RequestParam(required = false) String name,
			@RequestParam(value = "mode", defaultValue = "public") String modeString,
			@RequestParam(value = "label", defaultValue = "") String label) {
		try {
			int mode = 1;
			if ("private".equalsIgnoreCase(modeString)) {
				mode = 0;
			}
			url = URLDecoder.decode(url, "UTF-8");
			if (StringUtils.isBlank(name) && url.contains(".") && url.matches(".*/[^/]+\\.[^/]+")) {
				name = url.replaceFirst(".*/([^/]+\\.[^/]+)", "$1");
			}
			if (StringUtils.isBlank(token)) {
				throw new DocServiceException("URL上传错误，请提供token参数！");
			}
			AppVo appPo = appService.getByToken(token);
			if (null == appPo || StringUtils.isBlank(appPo.getId())) {
				logger.error("不存在该应用，token=" + token);
				throw new DocServiceException(0, "不存在该应用！");
			}
			String app = appPo.getId();
			DocVo vo = docService.addUrl(app, null, name, url, mode, label);
			if (null == vo) {
				throw new DocServiceException("上传URL文件错误！");
			}
			String uuid = vo.getUuid();
			return "redirect:" + uuid;
		} catch (Exception e) {
			logger.error("view url(" + url + ") error: " + e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "404";
		}
	}
}
