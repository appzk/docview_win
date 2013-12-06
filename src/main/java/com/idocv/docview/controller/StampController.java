package com.idocv.docview.controller;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.idocv.docview.service.DocService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.vo.DocVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.WordVo;

@Controller
@RequestMapping("stamp")
public class StampController {
	
private static final Logger logger = LoggerFactory.getLogger(StampController.class);
	
	@Resource
	private DocService docService;

	@Resource
	private ViewService viewService;

	@RequestMapping("{uuid}")
	public String stamp(HttpServletRequest req, Model model,
			@PathVariable String uuid) {
		try {
			DocVo docVo = docService.getByUuid(uuid);
			String rid = docVo.getRid();
			String name = docVo.getName();
			name = name.substring(0, name.lastIndexOf(".")) + ".pdf";
			PageVo<WordVo> pdfPage = viewService.convertWord2PdfStamp(rid, null, 0, 0);
			WordVo pdfWordVo = pdfPage.getData().get(0);
			File pdfFile = pdfWordVo.getDestFile();
			String url = "file:///" + pdfFile.getAbsolutePath();
			DocVo newVo = docService.addUrl("test", null, name, url, 1, null);
			String newUuid = newVo.getUuid();
			return "redirect:" + newUuid + "/edit";
		} catch (Exception e) {
			logger.error("view stamp(" + uuid + ") error: " + e.getMessage());
			return "404";
		}
	}
	
	@RequestMapping("{uuid}/edit")
	public ModelAndView stampEdit(HttpServletRequest req,
			ModelAndView model, @PathVariable String uuid) {
		try {
			model.setViewName("stamp/index");
			return model;
		} catch (Exception e) {
			logger.error("view stamp edit(" + uuid + ") error: " + e.getMessage());
			model.setViewName("404");
			model.addObject("error", e.getMessage());
			return model;
		}
	}
}