package com.idocv.docview.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("open")
public class OpenController {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenController.class);
	
	@RequestMapping("")
	public String all() {
		return "redirect:/open/all";
	}

	@RequestMapping("{label}")
	public String listLabel(@PathVariable(value = "label") String label) {
		return "doc/list-app";
	}
}