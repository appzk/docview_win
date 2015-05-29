package com.idocv.docview.controller;


import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.common.DocResponse;
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
	public Map<String, Object> get(
			@PathVariable(value = "uuid") String uuid,
			@RequestParam(value = "token") String token) {
		Map<String, Object> result = null;
		try {
			String sessionId = sessionService.add(token, uuid);
			result = DocResponse.getSuccessResponseMap();
			result.put("session", sessionId);
		} catch (Exception e) {
			logger.error("get session error: ", e);
			result = DocResponse.getErrorResponseMap(e.getMessage());
		}
		return result;
	}
}
