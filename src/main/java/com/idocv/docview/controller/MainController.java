package com.idocv.docview.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.service.AppService;

@Controller
@RequestMapping("")
public class MainController {

	private @Value("${docview.version}")
	String version;

	@Resource
	private AppService appService;

	@RequestMapping("version.json")
	@ResponseBody
	public Map<String, String> version() {
		Map<String, String> versionMap = new HashMap<String, String>();
		versionMap.put("version", version);
		return versionMap;
	}
}