package com.idocv.docview.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
//@RequestMapping("")
public class MainController {

	@RequestMapping("")
	public String HOME(HttpServletRequest req) {
		/*
		StringBuffer sb = req.getRequestURL();
		String app = sb.toString().replaceFirst("(http://)?(\\w+)\\.idocv.com/?.*", "$2");
		System.out.println(sb);
		return "redirect:/app/index.html";
		*/
		return "doc/list-app";
	}

}