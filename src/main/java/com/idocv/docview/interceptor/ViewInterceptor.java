package com.idocv.docview.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idocv.docview.util.RemoteUtil;

public class ViewInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ViewInterceptor.class);
	
	@Value("${thd.view.check.switch}")
	private boolean thdViewCheckSwitch = false;

	@Value("${thd.view.check.url}")
	private String thdViewCheckUrl;

	@Value("${thd.view.check.key.name}")
	private String thdViewCheckKeyName;

	@Value("${thd.view.check.default}")
	private String thdViewCheckDefault;

	private static ObjectMapper om = new ObjectMapper();

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView model) throws Exception {
		String requestUri = request.getRequestURI();
		if (thdViewCheckSwitch && requestUri.startsWith("/view/") && requestUri.matches("/view/\\w{4,10}")) {
			// default value
			Map<String, String> defaultMap = om.readValue(thdViewCheckDefault, new TypeReference<HashMap<String, String>>() { });;
			String read = defaultMap.get("read");
			String down = defaultMap.get("down");
			String copy = defaultMap.get("copy");

			// remote value
			String thdViewCheckKeyValue = request.getParameter(thdViewCheckKeyName);
			String uuid = requestUri.replaceFirst("/view/(\\w{4,10})", "$1");
			String checkUrl = thdViewCheckUrl + "?" + thdViewCheckKeyName + "=" + thdViewCheckKeyValue;
			try {
				String str = RemoteUtil.get(checkUrl);
				logger.info("[REMOTE GET] URL(" + checkUrl + "), RET(" + str + ")");
				Map<String, String> remoteMap = om.readValue(str, new TypeReference<HashMap<String, String>>() { });
				String remoteRead = remoteMap.get("read");
				String remoteDown = remoteMap.get("down");
				String remoteCopy = remoteMap.get("copy");

				if (StringUtils.isNotBlank(remoteRead) && remoteRead.matches("\\d{1,}")) {
					read = remoteRead;
				}
				if (StringUtils.isNotBlank(remoteDown) && remoteDown.matches("\\d{1,}")) {
					down = remoteDown;
				}
				if (StringUtils.isNotBlank(remoteCopy) && remoteCopy.matches("\\d{1,}")) {
					copy = remoteCopy;
				}
			} catch (Exception e) {
				logger.warn("[REMOTE GET] URL(" + checkUrl + "), EXCEPTION(" + e.getMessage() + ")");
				
			}
			response.addCookie(new Cookie("IDOCV_THD_VIEW_CHECK_READ_" + uuid, read));
			response.addCookie(new Cookie("IDOCV_THD_VIEW_CHECK_DOWN_" + uuid, down));
			response.addCookie(new Cookie("IDOCV_THD_VIEW_CHECK_COPY_" + uuid, copy));
		}
	}
}