package idocv.docview.controller;

import idocv.docview.common.DocServiceException;
import idocv.docview.common.IpUtil;
import idocv.docview.po.DocPo;
import idocv.docview.service.DocService;
import idocv.docview.service.PreviewService;
import idocv.docview.util.RcUtil;
import idocv.docview.vo.OfficeBaseVo;
import idocv.docview.vo.PageVo;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;



@Controller
@RequestMapping("view")
public class PreviewController {
	
	private static final Logger logger = LoggerFactory.getLogger(PreviewController.class);
	
	@Resource
	private DocService docService;

	@Resource
	private PreviewService previewService;
	
	/**
	 * 上传并预览
	 * 
	 * @param file
	 * @return
	 */
	@RequestMapping("upview")
	public String upView(ModelAndView model, HttpServletRequest req,
			@RequestParam(value = "file", required = true) MultipartFile file) {
		try {
			// upload
			String ip = IpUtil.getIpAddr(req);
			if (!previewService.validateIp(ip)) {
				System.err.println("IP: " + ip);
				// TODO
			}
			byte[] data = file.getBytes();
			String name = file.getOriginalFilename();
			DocPo po = docService.save(ip, name, data);
			logger.info("--> " + ip + " ADD " + po.getRid());
			String rid = po.getRid();

			// view
			return "redirect:" + rid + ".html";
		} catch (Exception e) {
			logger.error("upload error <controller>: ", e);
			return "error";
		}
	}

	@RequestMapping("{rid}.html")
	public ModelAndView freeView(
			@RequestParam(defaultValue = "free") String template,
			@PathVariable String rid,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size) {
		ModelAndView model = new ModelAndView();
		if (!RcUtil.isValidRid(rid)) {
			model.setViewName("error");
			model.addObject("msg", "非法资源ID！");
		}
		String ext = RcUtil.getExt(rid);
		PageVo<? extends Serializable> page = null;
		try {
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)
					|| "odt".equalsIgnoreCase(ext)) {
				page = previewService.convertWord2Html(rid, start, size);
				model.setViewName("free/viewDoc");
			} else if ("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext)
					|| "ods".equalsIgnoreCase(ext)) {
				page = previewService.convertExcel2Html(rid, start, size);
				model.setViewName("free/viewExcel");
			} else if ("ppt".equalsIgnoreCase(ext) || "pptx".equalsIgnoreCase(ext)
					|| "odp".equalsIgnoreCase(ext)) {
				page = previewService.convertPPT2Html(rid, start, size);
				model.setViewName("free/viewPPT");
			} else if ("txt".equalsIgnoreCase(ext)) {
				page = previewService.getTxtContent(rid);
				model.setViewName("free/viewTxt");
			} else if ("pdf".equalsIgnoreCase(ext)) {
				String url = previewService.convertPdf2Swf(rid);
				model.setViewName("free/viewPdf");
				model.addObject("url", url);
			} else {
				page = new PageVo<OfficeBaseVo>(null, 0);
				page.setCode(0);
				page.setDesc("Error: not a document type.");
			}
			DocPo po = docService.get(rid);
			if (null != po && null != page) {
				page.setName(po.getName());
				page.setRid(po.getRid());
				if (0 == page.getCode()) {
					model.setViewName("error");
					model.addObject("msg", "该文档无法预览，请下载查看！");
				}
			}
		} catch (DocServiceException e) {
			logger.error("freeView error: ", e);
			model.setViewName("error");
			model.addObject("msg", "该文档无法预览，请下载查看！");
		}
		model.addObject("page", page);
		return model;
	}

	@RequestMapping("sync/{rid}.html")
	public ModelAndView syncPreviewHtml(@PathVariable String rid,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size) {
		ModelAndView model = new ModelAndView();
		String[] elements = rid.split("_");
		if (StringUtils.isBlank(rid) || rid.split("_").length < 3) {
			PageVo<OfficeBaseVo> page = new PageVo<OfficeBaseVo>(null, 0);
			page.setCode(0);
			page.setDesc("Error: not a valid RID.");
			return null;
		}

		if (StringUtils.isBlank(rid) || rid.split("_").length < 3) {
			PageVo<OfficeBaseVo> page = new PageVo<OfficeBaseVo>(null, 0);
			page.setCode(0);
			page.setDesc("Error: not a valid RID.");
			return null;
		}

		String ext = elements[elements.length - 1];
		PageVo<? extends Serializable> page = null;
		try {
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)
					|| "odt".equalsIgnoreCase(ext)) {
				page = previewService.convertWord2Html(rid, start, size);
				model.setViewName("previewDoc");
			} else if ("xls".equalsIgnoreCase(ext)
					|| "xlsx".equalsIgnoreCase(ext)
					|| "ods".equalsIgnoreCase(ext)) {
				page = previewService.convertExcel2Html(rid, start, size);
				model.setViewName("previewExcel");
			} else if ("ppt".equalsIgnoreCase(ext)
					|| "pptx".equalsIgnoreCase(ext)
					|| "odp".equalsIgnoreCase(ext)) {
				page = previewService.convertPPT2Html(rid, start, size);
				model.setViewName("previewSPPT");
			} else if ("txt".equalsIgnoreCase(ext)) {
				page = previewService.getTxtContent(rid);
				model.setViewName("previewTxt");
			} else if ("pdf".equalsIgnoreCase(ext)) {
				String url = previewService.convertPdf2Swf(rid);
				model.setViewName("previewPdf");
				model.addObject("url", url);
			} else {
				page = new PageVo<OfficeBaseVo>(null, 0);
				page.setCode(0);
				page.setDesc("Error: not a document type.");
			}
			DocPo po = docService.get(rid);
			if (null != po && null != page) {
				page.setName(po.getName());
				page.setRid(po.getRid());
				if (0 == page.getCode()) {
					model.setViewName("error");
					model.addObject("msg", "该文档无法预览，请下载查看！");
				}
			}
		} catch (DocServiceException e) {
			logger.error("syncPreviewHtml error: ", e);
			model.setViewName("error");
			model.addObject("msg", "该文档无法预览，请下载查看！");
		}
		model.addObject("page", page);
		return model;
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
			DocPo po = docService.saveUrl(ip, url, name);
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