package com.idocv.docview.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ShareController {
	
	private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

	@Value("${view.page.share}")
	private boolean viewPageShare;

	@RequestMapping("share")
	public String shareAll() {
		if (viewPageShare) {
			return "redirect:/share/all";
		} else {
			return "404";
		}
	}

	@RequestMapping("share/{label}")
	public String shareLabel(@PathVariable(value = "label") String label) {
		if (viewPageShare) {
			return "doc/list-share";
		} else {
			return "404";
		}
	}
}