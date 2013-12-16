package com.idocv.docview.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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

import com.idocv.docview.common.DocResponse;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.PDFUtil;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.PdfVo;

@Controller
@RequestMapping("stamp")
public class StampController {
	
private static final Logger logger = LoggerFactory.getLogger(StampController.class);
	
	@Resource
	private RcUtil rcUtil;

	@Resource
	private DocService docService;

	@Resource
	private ViewService viewService;

	/**
	 * Stamp word, convert to PDF
	 * 
	 * @param req
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("word/{uuid}")
	public String stampWord(HttpServletRequest req, Model model,
			@PathVariable String uuid) {
		try {
			DocVo docVo = docService.getByUuid(uuid);
			String rid = docVo.getRid();
			String name = docVo.getName();
			name = name.substring(0, name.lastIndexOf(".")) + ".pdf";
			PageVo<PdfVo> pdfPage = viewService.convertWord2PdfStamp(rid, null, 0, 0);
			PdfVo pdfWordVo = pdfPage.getData().get(0);
			File pdfFile = pdfWordVo.getDestFile();
			String url = "file:///" + pdfFile.getAbsolutePath();
			DocVo newVo = docService.addUrl("test", null, name, url, 1, null);
			String newUuid = newVo.getUuid();
			return "redirect:" + uuid + "/" + newUuid + "/edit";
		} catch (Exception e) {
			logger.error("view stamp(" + uuid + ") error: " + e.getMessage());
			return "404";
		}
	}
	
	/**
	 * Stamp word edit page.
	 * 
	 * @param req
	 * @param model
	 * @param srcUuid
	 * @param destUuid
	 * @return
	 */
	@RequestMapping("word/{srcUuid}/{destUuid}/edit")
	public ModelAndView stampWordEdit(HttpServletRequest req,
			ModelAndView model, @PathVariable String srcUuid,
			@PathVariable String destUuid) {
		try {
			model.setViewName("stamp/word");
			return model;
		} catch (Exception e) {
			logger.error("view stamp edit(" + srcUuid + "-" + destUuid + ") error: " + e.getMessage());
			model.setViewName("404");
			model.addObject("error", e.getMessage());
			return model;
		}
	}

	/**
	 * Load PDF Stamp edit page
	 * 
	 * @param req
	 * @param model
	 * @param uuid
	 * @return
	 */
	@RequestMapping("pdf/{uuid}")
	public ModelAndView stampPdfEdit(HttpServletRequest req,
			ModelAndView model, @PathVariable String uuid) {
		try {
			model.setViewName("stamp/pdf");
			return model;
		} catch (Exception e) {
			logger.error("view stamp pdf edit(" + uuid + ") error: " + e.getMessage());
			model.setViewName("404");
			model.addObject("error", e.getMessage());
			return model;
		}
	}

	/**
	 * Download the Stamped PDF
	 * 
	 * @param req
	 * @param resp
	 * @param uuid
	 * @param stamp
	 * @param x
	 * @param y
	 */
	@RequestMapping("pdf/{uuid}/down")
	public void downloadPdfByUuid(HttpServletRequest req,
			HttpServletResponse resp,
			@PathVariable(value = "uuid") String uuid,
			@RequestParam(value = "stamp", required = false) String stamp,
			@RequestParam(value = "x", defaultValue = "0") float x,
			@RequestParam(value = "y", defaultValue = "0") float y) {
		try {
			DocVo vo = docService.getByUuid(uuid);
			String rid = vo.getRid();
			PageVo<PdfVo> page = viewService.pdfStamp(rid, stamp, x, y);
			if (null == page || CollectionUtils.isEmpty(page.getData())) {
				return;
			}
			PdfVo pdfVo = page.getData().get(0);
			File destFile = pdfVo.getDestFile();
			String nameRaw = vo.getName();
			String name = nameRaw.substring(0, nameRaw.lastIndexOf("."));
			name = name + destFile.getName().substring(destFile.getName().indexOf("sign") - 1);
			DocResponse.setResponseHeaders(req, resp, name);
			IOUtils.write(FileUtils.readFileToByteArray(destFile), resp.getOutputStream());
			docService.logDownload(uuid);
		} catch (Exception e) {
			logger.error("pdf stamp download error: " + e.getMessage());
		}
	}

	/**
	 * Validate a PDF
	 * 
	 * @param uuid
	 */
	@RequestMapping("pdf/{uuid}/validate")
	@ResponseBody
	public Map<String, String> validatePdf(@PathVariable(value = "uuid") String uuid) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("code", "1");
		result.put("desc", "正常！");
		try {
			DocVo vo = docService.getByUuid(uuid);
			String rid = vo.getRid();
			String src = rcUtil.getPath(rid);
			File srcFile = new File(src);
			if (!srcFile.isFile()) {
				throw new DocServiceException("源文件未找到！");
			}
			String srcMd5 = PDFUtil.getMd5(srcFile);
			Map<String, String> metas = PDFUtil.getPDFMetas(srcFile);
			String isStamp = metas.get("stamp");
			if (StringUtils.isBlank(isStamp) || !"1".equals(isStamp)) {
				result.put("desc", "该PDF未被签章！");
				return result;
			}
			
			Path path = Paths.get(src);
			BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
			FileTime ftCreate = attrs.creationTime();
			FileTime ftLastModified = attrs.lastModifiedTime();
			long diffTime = ftCreate.toMillis() - ftLastModified.toMillis();
			diffTime = diffTime > 0 ? diffTime : -diffTime;
			if (diffTime > 10000) {
				result.put("code", "0");
				result.put("desc", "该签章PDF已被篡改！");
				return result;
			}
			result.put("desc", "该签章PDF未被篡改！");
			return result;
		} catch (Exception e) {
			logger.error("Validate PDF error: " + e.getMessage());
			return result;
		}
	}
}