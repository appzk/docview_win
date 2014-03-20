package com.idocv.docview.controller;

import java.util.HashMap;
import java.util.Map;

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
	public Map<String, String> signUp(
			HttpServletRequest req,
			@RequestParam(value = "appkey", defaultValue = "testtoken") String appkey,
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "email") String email) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			UserVo vo = userService.add(appkey, username, password, email);
			map.put("uid", vo.getId());
			map.put("sid", vo.getSid());
		} catch (Exception e) {
			logger.error("user signup error <controller>: " + e.getMessage());
			map.put("error", e.getMessage());
		}
		return map;
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
	public Map<String, String> activateJson(
			@RequestParam(value = "email") String email,
			@RequestParam(value = "key") String key) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			UserVo vo = userService.activate(email, key);
			if (null == vo) {
				map.put("error", "用户不存在！");
				return map;
			}
			map.put("uid", vo.getId());
			map.put("status", String.valueOf(vo.getStatus()));
			return map;
		} catch (Exception e) {
			logger.error("user activate error <controller>: " + e.getMessage());
			map.put("error", e.getMessage());
			return map;
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
	public Map<String, String> login(String user, String password) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			UserVo vo = userService.login(user, password);
			if (null != vo) {
				map.put("sid", vo.getSid());
			} else {
				map.put("error", "登陆失败！");
			}
		} catch (DocServiceException e) {
			logger.error("user login.json error <controller>: " + e.getMessage());
			map.put("error", e.getMessage());
		}
		return map;
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
	public Map<String, String> logout(HttpServletRequest req) {
		Map<String, String> map = new HashMap<String, String>();
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
			map.put("success", "Logged out!");
		} catch (DocServiceException e) {
			logger.error("user logout.json error <controller>: " + e.getMessage());
			map.put("error", e.getMessage());
		}
		return map;
	}

	@ResponseBody
	@RequestMapping("checkLogin.json")
	public Map<String, String> checkLogin(HttpServletRequest req) {
		Map<String, String> map = new HashMap<String, String>();
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
				map.put("uid", vo.getId());
				map.put("sid", vo.getSid());
				map.put("username", vo.getUsername());
				return map;
			} else {
				map.put("error", "未登录！");
				return map;
			}
		} catch (DocServiceException e) {
			map.put("error", e.getMessage());
			return map;
		}
	}
}