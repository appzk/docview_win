package com.idocv.docview.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class XWController {

	@ResponseBody
	@RequestMapping("{appId}/{md5Filename}.{ext:[a-zA-Z]{3,4}}")
	public Map<String, String> test(
			@PathVariable(value = "appId") String appId,
			@PathVariable(value = "md5Filename") String md5Filename,
			@PathVariable(value = "ext") String ext) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("code", "1");
		result.put("app", appId);
		result.put("md5", md5Filename);
		result.put("ext", ext);
		return result;
	}

}