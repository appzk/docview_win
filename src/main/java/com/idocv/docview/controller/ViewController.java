package com.idocv.docview.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.idocv.docview.common.ViewType;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.AppService;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.SessionService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.IpUtil;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.AppVo;
import com.idocv.docview.vo.DocVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.SessionVo;
import com.idocv.docview.vo.ViewBaseVo;

@Controller
@RequestMapping("view")
public class ViewController {
	
	private static final Logger logger = LoggerFactory.getLogger(ViewController.class);
	
	@Resource
	private AppService appService;

	@Resource
	private DocService docService;

	@Resource
	private ViewService viewService;

	@Resource
	private SessionService sessionService;

	private @Value("${thd.view.template}")
	String thdViewTemplate;

	private @Value("${view.page.load.async}")
	boolean pageLoadAsync;
	
	private @Value("${view.page.word.style}")
	String pageWordStyle;
	
	private @Value("${view.page.excel.style}")
	String pageExcelStyle;
	
	private @Value("${view.page.pdf.style}") String pagePdfStyle;

	private @Value("${view.page.private.session.duraion}")
	int viewPagePrivateSessionDuraion;

	@Resource
	private RcUtil rcUtil;

	@RequestMapping("")
	public void home(HttpServletRequest req, HttpServletResponse resp) {
		String timeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println("VIEW HOME from " + IpUtil.getIpAddr(req) + " on " + timeString);
		resp.setStatus(301);
		resp.setHeader("Location", "http://www.idocv.com");
		resp.setHeader("Connection", "close");
		// return "redirect:http://www.idocv.com";
	}

	@RequestMapping("{md5:\\w{32}}")
	public String md5(ModelAndView model, @PathVariable String md5) {
		// md5 view
		String uuid = null;
		if (StringUtils.isNotBlank(md5) && (32 == md5.length())) {
			try {
				DocVo vo = docService.getByMd5(md5);
				if (null != vo && StringUtils.isNotBlank(vo.getUuid())) {
					uuid = vo.getUuid();
				}
			} catch (DocServiceException e) {
				logger.error("无法使用MD5方式预览文件" + md5);
			}
		}
		if (StringUtils.isNotBlank(uuid)) {
			return "redirect:" + uuid + (pageLoadAsync ? "" : ".html");
		} else {
			logger.error("view md5(" + md5 + ") error: 未找到相关文档");
			model.addObject("error", "未找到相关文档");
			return "404";
		}
	}

	@RequestMapping("{id:\\w{1,31}}")
	public ModelAndView page(ModelAndView model,
			@PathVariable String id,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(value = "type", required = false) String type) {
		String uuid = null;
		String sessionId = null;
		try {
			// check UUID
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

			// check extension
			DocVo docVo = docService.getByUuid(uuid);
			if (null == docVo || StringUtils.isBlank(docVo.getRid())) {
				throw new DocServiceException("文档(" + uuid + ")不存在！");
			}
			String rid = docVo.getRid();
			String ext = RcUtil.getExt(rid);
			if (StringUtils.isBlank(ext)) {
				logger.error("无法预览无后缀名的文件(" + uuid + ")！");
				throw new DocServiceException("无法预览无后缀名的文件(" + uuid + ")！");
			}
			if (!rcUtil.isSupportView(ext)) {
				logger.error("暂不支持预览文件" + uuid + "(" + ext + ")！");
				throw new DocServiceException("暂不支持预览文件" + uuid + "(" + ext
						+ ")！");
			}

			if (uuid.endsWith(ViewType.WORD.getSymbol())) {
				if ("img".equalsIgnoreCase(pageWordStyle)
						|| "pdf".equalsIgnoreCase(pageWordStyle)) {
					model.setViewName("word/pdf");
				} else if ("bookmark".equalsIgnoreCase(pageWordStyle)) {
					model.setViewName("word/bookmark");
				} else {
					model.setViewName("word/index");
				}
				if ("test".equals(type)) {
					model.setViewName("word/test");
				} else if ("watermark".equals(type)) {
					model.setViewName("word/test_wartermark");
				} else if ("entry".equalsIgnoreCase(type)) {
					model.setViewName("word/sync-main");
					return model;
				} else if ("speaker".equalsIgnoreCase(type)) {
					model.setViewName("word/sync-speaker");
					return model;
				} else if ("audience".equalsIgnoreCase(type)) {
					model.setViewName("word/sync-audience");
					return model;
				}

				// if type is set, use it.
				if ("img".equalsIgnoreCase(type)) {
					model.setViewName("word/pdf");
				} else if ("html".equalsIgnoreCase(type)) {
					model.setViewName("word/index");
				}
				return model;
			} else if (uuid.endsWith(ViewType.EXCEL.getSymbol())) {
				if ("img".equalsIgnoreCase(pageWordStyle)
						|| "pdf".equalsIgnoreCase(pageExcelStyle)) {
					model.setViewName("excel/pdf");
				} else {
					model.setViewName("excel/index");
				}

				// if type is set, use it.
				if ("img".equalsIgnoreCase(type)) {
					model.setViewName("excel/pdf");
				} else if ("html".equalsIgnoreCase(type)) {
					model.setViewName("excel/index");
				}
				return model;
			} else if (uuid.endsWith(ViewType.PPT.getSymbol())) {
				if ("3d".equalsIgnoreCase(type)) {
					model.setViewName("ppt/index");
					return model;
				} else if ("carousel".equalsIgnoreCase(type)) {
					model.setViewName("ppt/carousel");
					return model;
				} else if ("test".equalsIgnoreCase(type)) {
					model.setViewName("ppt/test");
					return model;
				} else if ("entry".equalsIgnoreCase(type)) {
					model.setViewName("ppt/sync-main");
					return model;
				} else if ("speaker".equalsIgnoreCase(type)) {
					model.setViewName("ppt/sync-speaker");
					return model;
				} else if ("audience".equalsIgnoreCase(type)) {
					model.setViewName("ppt/sync-audience");
					return model;
				} else {
					model.setViewName("ppt/index");
					return model;
				}
			} else if (uuid.endsWith(ViewType.PDF.getSymbol())) {
				if ("img".equalsIgnoreCase(pagePdfStyle)
						|| "pdf".equalsIgnoreCase(pagePdfStyle)) {
					model.setViewName("pdf/img");
				} else {
					model.setViewName("pdf/index");
				}

				// if type is set, use it.
				if ("img".equalsIgnoreCase(type)) {
					model.setViewName("pdf/img");
				} else if ("html".equalsIgnoreCase(type)) {
					model.setViewName("pdf/index");
				}
				return model;
			} else if (uuid.endsWith(ViewType.TXT.getSymbol())) {
				model.setViewName("txt/index");
				return model;
			} else if (uuid.endsWith(ViewType.AUDIO.getSymbol())) {
				model.setViewName("audio/index");
				return model;
			}
			if (StringUtils.isNotBlank(thdViewTemplate) && thdViewTemplate.contains(ext)) {
				// jpg,gif,png,bmp@image#mp3,midi@audio#avi,rmvb,mp4,mkv@video
				String templateName = thdViewTemplate.replaceFirst(".*?" + ext + ".*?@(\\w+).*", "$1");
				model.setViewName("template/" + templateName);
				String dfsUrl = docVo.getUrl();
				String appId = docVo.getApp();
				String uid = docVo.getUid();
				Map<String, Object> attMap = new HashMap<String, Object>();
				attMap.put("url", dfsUrl);
				attMap.put("app", appId);
				attMap.put("uid", uid);
				attMap.put("uuid", uuid);
				attMap.put("ext", ext);
				attMap.put("name", docVo.getName());
				attMap.put("size", docVo.getSize());
				if (StringUtils.isNotBlank(dfsUrl) && dfsUrl.matches(".*?/(\\w{32}).\\w+")) {
					String md5 = dfsUrl.replaceFirst(".*?/(\\w{32}).\\w+", "$1");
					attMap.put("md5", md5);
				}
				model.addAllObjects(attMap);
				return model;
			} else {
				throw new DocServiceException("未找到文件" + uuid + "(" + ext
						+ ")的预览模板，请联系管理员！");
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
	public PageVo<? extends Serializable> jsonUuid(HttpServletRequest req,
			@RequestParam(defaultValue = "default") String template,
			@PathVariable String id,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(value = "type", required = false) String type) {
		PageVo<? extends Serializable> page = null;
		String uuid = null;
		String rid = null;
		String session = null;
		try {
			if (StringUtils.isNotBlank(id) && id.endsWith(".html")) {
				id = id.substring(0, id.lastIndexOf(".html"));
			}
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
				if (currentTime - sessionCtime > (viewPagePrivateSessionDuraion * 60 * 1000)) {
					throw new DocServiceException("会话已过期，请重新获取一个会话！");
				}
				if (!uuid.equals(sessionVo.getUuid())) {
					throw new DocServiceException("该会话和文档不一致，无法预览！");
				}
			}
			if (ViewType.WORD == ViewType.getViewTypeByExt(ext)) {
				start = (start - 1) * size + 1;
				if ("pdf".equalsIgnoreCase(pageWordStyle)
						|| "speaker".equalsIgnoreCase(type)
						|| "audience".equalsIgnoreCase(type)) {
					page = viewService.convertWord2Img(rid, 1, 0);
				} else {
					page = viewService.convertWord2Html(rid, start, size);
				}

				// if type is set, use it.
				if ("img".equalsIgnoreCase(type)) {
					page = viewService.convertWord2Img(rid, 1, 0);
				} else if ("html".equalsIgnoreCase(type)) {
					page = viewService.convertWord2Html(rid, start, size);
				}
			} else if (ViewType.EXCEL == ViewType.getViewTypeByExt(ext)) {
				if ("img".equalsIgnoreCase(pageExcelStyle)
						|| "pdf".equalsIgnoreCase(pageExcelStyle)) {
					page = viewService.convertExcel2Img(rid, 1, 0);
				} else {
					page = viewService.convertExcel2Html(rid, start, size);
				}

				// if type is set, use it.
				if ("img".equalsIgnoreCase(type)) {
					page = viewService.convertExcel2Img(rid, 1, 0);
				} else if ("html".equalsIgnoreCase(type)) {
					page = viewService.convertExcel2Html(rid, start, size);
				}
			} else if (ViewType.PPT == ViewType.getViewTypeByExt(ext)) {
				page = viewService.convertPPT2Img(rid, start, size);
			} else if (ViewType.TXT == ViewType.getViewTypeByExt(ext)) {
				start = (start - 1) * size + 1;
				page = viewService.convertTxt2Html(rid, start, size);
			} else if (ViewType.PDF == ViewType.getViewTypeByExt(ext)) {
				// page = previewService.convertPdf2Html(rid, 1, 0);
				if ("img".equalsIgnoreCase(pagePdfStyle)
						|| "pdf".equalsIgnoreCase(pagePdfStyle)) {
					page = viewService.convertPdf2Img(rid, 1, 0);
				} else {
					page = viewService.convertPdf2Html(rid, 1, 0);
				}

				// if type is set, use it.
				if ("img".equalsIgnoreCase(type)) {
					page = viewService.convertPdf2Img(rid, 1, 0);
				} else if ("html".equalsIgnoreCase(type)) {
					page = viewService.convertPdf2Html(rid, 1, 0);
				}
			} else if (ViewType.IMG == ViewType.getViewTypeByExt(ext)) {
				page = viewService.convertImage2Jpg(rid);
			} else if (ViewType.AUDIO == ViewType.getViewTypeByExt(ext)) {
				page = viewService.convertAudio2Mp3(rid);
			} else {
				page = new PageVo<ViewBaseVo>(null, 0);
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
			page.setMd5(docVo.getMd5());
			page.setCtime(docVo.getCtime());
			docService.logView(uuid);
		} catch (Exception e) {
			logger.error("view id.json(" + id + ") error: " + e.getMessage());
			page = new PageVo<ViewBaseVo>(null, 0);
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
					logger.error("该会话与文档UUID不一致！" + ", session=" + session
							+ ", uuid=" + uuid);
					throw new DocServiceException("该会话与文档UUID不一致！");
				}
			}
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)
					|| "odt".equalsIgnoreCase(ext)) {
				page = viewService.convertWord2Html(rid, start, size);
			} else if ("xls".equalsIgnoreCase(ext)
					|| "xlsx".equalsIgnoreCase(ext)
					|| "ods".equalsIgnoreCase(ext)) {
				page = viewService.convertExcel2Html(rid, start, size);
			} else if ("ppt".equalsIgnoreCase(ext)
					|| "pptx".equalsIgnoreCase(ext)
					|| "odp".equalsIgnoreCase(ext)) {
				page = viewService.convertPPT2Img(rid, start, size);
			} else if ("pdf".equalsIgnoreCase(ext)) {
				page = viewService.convertPdf2Img(rid, 1, 0);
			} else if ("txt".equalsIgnoreCase(ext)) {
				page = viewService.convertTxt2Html(rid, start, size);
			} else {
				page = new PageVo<ViewBaseVo>(null, 0);
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
			page = new PageVo<ViewBaseVo>(null, 0);
			page.setCode(0);
			page.setDesc(e.getMessage());
			page.setUuid(uuid);
		}
		model.addObject("page", page);
		if (uuid.endsWith(ViewType.WORD.getSymbol())) {
			model.setViewName("word/static");
		} else if (uuid.endsWith(ViewType.EXCEL.getSymbol())) {
			model.setViewName("excel/static");
		} else if (uuid.endsWith(ViewType.PPT.getSymbol())) {
			model.setViewName("ppt/static");
		} else if (uuid.endsWith(ViewType.PDF.getSymbol())) {
			model.setViewName("pdf/static");
		} else if (uuid.endsWith(ViewType.TXT.getSymbol())) {
			model.setViewName("txt/static");
		} else {
			logger.error("view uuid.html(" + uuid + ") error: 未知文件格式！");
			model.setViewName("404");
		}
		return model;
	}
	
	@RequestMapping("url")
	public String url(
			HttpServletRequest req,
			Model model,
			@RequestParam(required = false) String url,
			@RequestParam(value = "md5", required = false) String md5,
			@RequestParam(value = "token", defaultValue = "testtoken") String token,
			@RequestParam(required = false) String name,
			@RequestParam(value = "mode", defaultValue = "public") String modeString,
			@RequestParam(value = "label", defaultValue = "") String label) {
		try {
			int mode = 1;
			if ("private".equalsIgnoreCase(modeString)) {
				mode = 0;
			}

			// md5 view
			if (StringUtils.isNotBlank(md5)) {
				DocVo vo = docService.getByMd5(md5);
				if (null != vo && StringUtils.isNotBlank(vo.getUuid())) {
					String uuid = vo.getUuid();
					return "redirect:" + uuid + (pageLoadAsync ? "" : ".html");
				}
			}

			if (StringUtils.isBlank(url)) {
				throw new DocServiceException("URL上传错误，请提供URL参数！");
			}

			// url = URLDecoder.decode(url, "UTF-8"); // 已经decode，无需再次decode
			
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
			return "redirect:" + uuid + (pageLoadAsync ? "" : ".html");
		} catch (Exception e) {
			logger.error("view url(" + url + ") error: " + e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "404";
		}
	}
}