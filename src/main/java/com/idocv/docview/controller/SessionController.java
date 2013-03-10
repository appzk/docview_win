package com.idocv.docview.controller;


import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.service.SessionService;


@Controller
@RequestMapping("session")
public class SessionController {
	
	private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

	@Resource
	private SessionService sessionService;

	/**
	 * Get view doc session id
	 */
	@ResponseBody
	@RequestMapping("{uuid}")
	public String get(
			@PathVariable(value = "uuid") String uuid,
			@RequestParam(value = "token") String token) {
		try {
			String sessionId = sessionService.add(token, uuid);
			return "{\"session\":\"" + sessionId + "\"}";
		} catch (Exception e) {
			logger.error("get session error: ", e);
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}
}
