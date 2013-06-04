package com.idocv.docview.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.AppService;
import com.idocv.docview.vo.AppVo;

@Controller
@RequestMapping("")
public class MainController {

	@Resource
	private AppService appService;
	
	@RequestMapping("")
	public String HOME(HttpServletRequest req) {
		try {
			StringBuffer sb = req.getRequestURL();
			String app = sb.toString().replaceFirst("(http://)?(\\w+)\\.idocv.com/?.*", "$2");
			System.out.println(sb);
			boolean existApp = false;
			AppVo appPo = appService.get(app);
			if (null != appPo) {
				existApp = true;
			}
			if (existApp) {
				return "redirect:/open/all";
			} else {
				return "app/list";
			}
		} catch (DocServiceException e) {
			e.printStackTrace();
			return "app/list";
		}
	}

}