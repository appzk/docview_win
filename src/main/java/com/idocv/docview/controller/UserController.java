package com.idocv.docview.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.service.UserService;
import com.idocv.docview.vo.UserVo;

@Controller
@RequestMapping("user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Resource
	private UserService userService;
	
	/**
	 * Sign up
	 * 
	 * @param req
	 * @param username
	 * @param password
	 * @param email
	 * @return
	 */
	@ResponseBody
	@RequestMapping("signup")
	public String signUp(HttpServletRequest req,
			@RequestParam(value = "appkey", defaultValue = "wevkey") String appkey,
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "email") String email) {
		try {
			UserVo vo = userService.signUp(appkey, username, password, email);
			return "{\"uid\":\"" + vo.getId() + "\"}";
		} catch (Exception e) {
			logger.error("sign up error <controller>: ", e);
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}
}