package com.idocv.docview.controller;

import java.io.Serializable;
import java.net.URLDecoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.DocPo;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.PreviewService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.OfficeBaseVo;
import com.idocv.docview.vo.PageVo;

@Controller
@RequestMapping("v")
public class VController {
	
	private static final Logger logger = LoggerFactory.getLogger(VController.class);
	
	@Resource
	private DocService docService;

	@Resource
	private PreviewService previewService;

	@RequestMapping("")
	public String home() {
		return "redirect:http://www.idocv.com";
	}

	@RequestMapping("{uuid}")
	public ModelAndView viewUuid(
			@RequestParam(defaultValue = "default") String template,
			@PathVariable String uuid,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size) {
		ModelAndView model = new ModelAndView();
		PageVo<? extends Serializable> page = null;
		try {
			DocPo po = docService.getByUuid(uuid);
			if (null == po || StringUtils.isBlank(po.getRid())) {
				throw new DocServiceException("Document NOT found!");
			}
			String rid = po.getRid();
			String ext = RcUtil.getExt(rid);
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)
					|| "odt".equalsIgnoreCase(ext)) {
				page = previewService.convertWord2Html(rid, start, size);
				model.setViewName("word/default");
			} else if ("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext)
					|| "ods".equalsIgnoreCase(ext)) {
				page = previewService.convertExcel2Html(rid, start, size);
				model.setViewName("excel/default");
			} else if ("ppt".equalsIgnoreCase(ext) || "pptx".equalsIgnoreCase(ext)
					|| "odp".equalsIgnoreCase(ext)) {
				page = previewService.convertPPT2Html(rid, start, size);
				model.setViewName("ppt/reveal");
			} else if ("txt".equalsIgnoreCase(ext)) {
				page = previewService.getTxtContent(rid);
				model.setViewName("txt/default");
			} else if ("pdf".equalsIgnoreCase(ext)) {
				String url = previewService.convertPdf2Swf(rid);
				model.setViewName("pdf/default");
				model.addObject("url", url);
			} else {
				page = new PageVo<OfficeBaseVo>(null, 0);
				page.setCode(0);
				page.setDesc("Error: not a document type.");
			}
			page.setName(po.getName());
			page.setRid(po.getRid());
		} catch (Exception e) {
			logger.error("freeView error: ", e);
			model.setViewName("error");
			model.addObject("msg", "Can't preview, please download it.");
		}
		model.addObject("page", page);
		return model;
	}

	@RequestMapping("{uuid}.json")
	@ResponseBody
	public PageVo<? extends Serializable> jsonUuid(
			@RequestParam(defaultValue = "default") String template,
			@PathVariable String uuid,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size) {
		PageVo<? extends Serializable> page = null;
		try {
			DocPo po = docService.getByUuid(uuid);
			if (null == po || StringUtils.isBlank(po.getRid())) {
				throw new DocServiceException("Document NOT found!");
			}
			String rid = po.getRid();
			String ext = RcUtil.getExt(rid);
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
				page = previewService.getTxtContent(rid);
			} else if ("pdf".equalsIgnoreCase(ext)) {
				// TODO
				String url = previewService.convertPdf2Swf(rid);
			} else {
				page = new PageVo<OfficeBaseVo>(null, 0);
				page.setCode(0);
				page.setDesc("Error: not a document type.");
			}
			page.setName(po.getName());
			page.setRid(po.getRid());
		} catch (Exception e) {
			logger.error("freeView error: ", e);
		}
		return page;
	}

	@RequestMapping("url")
	public String previewUrl(HttpServletRequest req,
			@RequestParam(required = true) String url,
			@RequestParam(required = false) String name) {
		try {
			url = URLDecoder.decode(url, "UTF-8");
			if (StringUtils.isBlank(name) && url.contains(".") && url.matches(".*/[^/]+\\.[^/]+")) {
				name = url.replaceFirst(".*/([^/]+\\.[^/]+)", "$1");
			}
			String ip = req.getRemoteAddr();
			String appKey = "doctest";
			DocPo po = docService.addUrl(appKey, url, name);
			if (null != po) {
				String rid = po.getRid();
				return "redirect:" + rid + ".html";
			} else {
				return "error";
			}
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
