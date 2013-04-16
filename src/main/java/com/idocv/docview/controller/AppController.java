package com.idocv.docview.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("app")
public class AppController {
	
	private static final Logger logger = LoggerFactory.getLogger(AppController.class);
	
	/*
	@RequestMapping("{label}")
	public String listLabel(HttpServletRequest req,
			@PathVariable(value = "label") String label) {
		return "doc/list-app";
	}
	*/
}