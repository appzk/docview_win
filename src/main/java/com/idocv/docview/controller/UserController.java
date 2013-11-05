package com.idocv.docview.controller;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@RequestMapping("{username}")
	public String home(@PathVariable(value = "username") String username) {
		return "doc/list-user";
	}

	@RequestMapping("{username}/{label}")
	public String label(@PathVariable(value = "username") String username,
			@PathVariable(value = "label") String label) {
		return "doc/list-user";
	}

	/**
	 * Sign up page
	 * 
	 * @return
	 */
	@RequestMapping("signup")
	public String signUpPage() {
		return "user/signup";
	}

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
	@RequestMapping("signup.json")
	public String signUp(HttpServletRequest req,
			@RequestParam(value = "appkey", defaultValue = "testtoken") String appkey,
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "email") String email) {
		try {
			UserVo vo = userService.add(appkey, username, password, email);
			return "{\"uid\":\"" + vo.getId() + "\", \"sid\":\"" + vo.getSid() + "\"}";
		} catch (Exception e) {
			logger.error("user signup error <controller>: " + e.getMessage());
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}

	/**
	 * Load Email address validation page
	 * 
	 * @param req
	 * @param email
	 * @param key
	 * @return
	 */
	@RequestMapping("activate")
	public String activatePage() {
		return "user/activate";
	}

	/**
	 * Validate Email address
	 * 
	 * @param req
	 * @param email
	 * @param key
	 * @return
	 */
	@ResponseBody
	@RequestMapping("activate.json")
	public String activateJson(
			@RequestParam(value = "email") String email,
			@RequestParam(value = "key") String key) {
		try {
			UserVo vo = userService.activate(email, key);
			if (null == vo) {
				return "{\"error\":\"用户不存在！\"}";
			}
			return "{\"uid\":\"" + vo.getId() + "\", \"status\":\"" + vo.getStatus() + "\"}";
		} catch (Exception e) {
			logger.error("user activate error <controller>: " + e.getMessage());
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}

	/**
	 * Login
	 * 
	 * @return
	 */
	@RequestMapping("login")
	public String loginPage() {
		return "user/login";
	}

	/**
	 * login
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	@ResponseBody
	@RequestMapping("login.json")
	public String login(String user, String password) {
		try {
			UserVo vo = userService.login(user, password);
			if (null != vo) {
				return "{\"sid\":\"" + vo.getSid() + "\"}";
			} else {
				return "{\"error\":\"Can NOT login!\"}";
			}
		} catch (DocServiceException e) {
			logger.error("user login.json error <controller>: " + e.getMessage());
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}

	/**
	 * logout
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	@ResponseBody
	@RequestMapping("logout.json")
	public String logout(HttpServletRequest req) {
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
			userService.logout(sid);
			return "{\"success\":\"Logged out!\"}";
		} catch (DocServiceException e) {
			logger.error("user logout.json error <controller>: " + e.getMessage());
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}

	@ResponseBody
	@RequestMapping("checkLogin.json")
	public String checkLogin(HttpServletRequest req) {
		try {
			Cookie[] cookies = req.getCookies();
			String sid = null;
			for (Cookie cookie : cookies) {
				if ("IDOCVSID".equalsIgnoreCase(cookie.getName())) {
					sid = cookie.getValue();
					break;
				}
			}
			if (StringUtils.isBlank(sid)) {
				throw new DocServiceException("未登录！");
			}
			UserVo vo = userService.getBySid(sid);
			if (null != vo) {
				return "{\"uid\":\"" + vo.getId() + "\"," +
						"\"sid\":\"" + vo.getSid() + "\"," +
						"\"username\":\"" + vo.getUsername() + "\"}";
			} else {
				return "{\"error\":\"未登录！\"}";
			}
		} catch (DocServiceException e) {
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}
}