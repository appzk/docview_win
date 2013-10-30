package com.idocv.docview.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.AppService;
import com.idocv.docview.vo.AppVo;

@Controller
@RequestMapping("")
public class MainController {

	private @Value("${docview.version}")
	String version;

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
				return "index";
			}
		} catch (DocServiceException e) {
			e.printStackTrace();
			return "index";
		}
	}

	@RequestMapping("version.json")
	@ResponseBody
	public Map<String, String> version() {
		Map<String, String> versionMap = new HashMap<String, String>();
		versionMap.put("version", version);
		return versionMap;
	}

}