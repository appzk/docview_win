package com.idocv.docview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class MainController {

	@RequestMapping("")
	public String HOME() {
		return "redirect:/index.html";
	}

}