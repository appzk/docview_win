package com.idocv.docview.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ShareController {
	
	private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

	@RequestMapping("share")
	public String shareAll() {
		return "redirect:/share/all";
	}

	@RequestMapping("share/{label}")
	public String shareLabel(@PathVariable(value = "label") String label) {
		return "doc/list-share";
	}
}