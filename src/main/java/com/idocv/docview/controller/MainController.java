package com.idocv.docview.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class MainController {

	@RequestMapping("")
	public String HOME(HttpServletRequest req) {
		StringBuffer sb = req.getRequestURL();
		String app = sb.toString().replaceFirst("(http://)?(\\w+)\\.idocv.com/?.*", "$2");
		System.out.println(sb);
		boolean existApp = false;
		// TODO query app
		if (existApp) {
			return "doc/list-app";
		} else {
			return "app/list";
		}
		// else -> return "doc/list-app";
	}

}