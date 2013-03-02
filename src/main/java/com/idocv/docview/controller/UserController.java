package com.idocv.docview.controller;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.exception.DocServiceException;
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
			return "{\"uid\":\"" + vo.getId() + "\", \"sid\":\"" + vo.getSid() + "\"}";
		} catch (Exception e) {
			logger.error("sign up error <controller>: ", e);
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}

	/**
	 * login
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	@ResponseBody
	@RequestMapping("login")
	public String login(String user, String password) {
		try {
			UserVo vo = userService.login(user, password);
			if (null != vo) {
				return "{\"sid\":\"" + vo.getSid() + "\"}";
			} else {
				return "{\"error\":\"Can NOT login!\"}";
			}
		} catch (DocServiceException e) {
			logger.error("login error <controller>: ", e);
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}
	
	@ResponseBody
	@RequestMapping("checkLogin")
	public String checkLogin(HttpServletRequest req) {
		try {
			Cookie[] cookies = req.getCookies();
			String sid = null;
			for (Cookie cookie : cookies) {
				if ("IDOCVSID".equalsIgnoreCase(cookie.getName())) {
					sid = cookie.getValue();
				}
			}
			if (StringUtils.isBlank(sid)) {
				throw new DocServiceException("NOT logged in!");
			}
			UserVo vo = userService.getBySid(sid);
			if (null != vo) {
				return "{\"uid\":\"" + vo.getId() + "\"," +
						"\"sid\":\"" + vo.getSid() + "\"," +
						"\"username\":\"" + vo.getUsername() + "\"}";
			} else {
				return "{\"error\":\"NOT logged in!\"}";
			}
		} catch (DocServiceException e) {
			logger.error("login error <controller>: ", e);
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}
}