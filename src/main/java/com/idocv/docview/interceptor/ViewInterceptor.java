package com.idocv.docview.interceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idocv.docview.common.DocResponse;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.SessionService;
import com.idocv.docview.service.UserService;
import com.idocv.docview.util.RemoteUtil;
import com.idocv.docview.vo.SessionVo;
import com.idocv.docview.vo.UserVo;

public class ViewInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ViewInterceptor.class);
	
	@Resource
	private UserService userService;

	@Resource
	private SessionService sessionService;

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
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String requestUri = request.getRequestURI();
		if (thdViewCheckSwitch) {
			// upload
			if (requestUri.startsWith("/doc/upload")) {
				Map<String, String> authMap = getRemoteAuthMap(request);
				String uploadAuth = authMap.get("upload");
				if ("0".equals(uploadAuth)) {
					Map<String, Object> respMap = DocResponse
							.getErrorResponseMap("没有上传权限");
					response.getWriter().write(JSON.toJSONString(respMap));
					return false;
				}
			}
			// view
			if (requestUri.startsWith("/view/") && requestUri.matches("/view/\\w{4,31}.json")) {
				Map<String, String> authMap = getRemoteAuthMap(request);
				String viewAuth = authMap.get("view");
				if ("0".equals(viewAuth)) {
					Map<String, Object> respMap = DocResponse
							.getErrorResponseMap("没有预览权限");
					response.getWriter().write(JSON.toJSONString(respMap));
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView model) throws Exception {
		String requestUri = request.getRequestURI();
		if (thdViewCheckSwitch) {
			// read, down and copy
			if (requestUri.startsWith("/view/") && requestUri.matches("/view/\\w{4,31}")) {
				// remote value
				String uuid = requestUri.replaceFirst("/view/(\\w{4,31})", "$1");
				if (StringUtils.isNotBlank(uuid) && uuid.matches("\\w{24}")) {
					uuid = getUuidBySessionId(uuid);
				}
				Map<String, String> authMap = getRemoteAuthMap(request);
				response.addCookie(new Cookie("IDOCV_THD_VIEW_CHECK_READ_" + uuid, authMap.get("read")));
				response.addCookie(new Cookie("IDOCV_THD_VIEW_CHECK_DOWN_" + uuid, authMap.get("down")));
				response.addCookie(new Cookie("IDOCV_THD_VIEW_CHECK_COPY_" + uuid, authMap.get("copy")));
				response.addCookie(new Cookie("IDOCV_THD_VIEW_CHECK_INFO_" + uuid, URLEncoder.encode(authMap.get("info"), "UTF-8")));
			}
		}
	}
	
	public String getUuidBySessionId(String sessionId) throws DocServiceException {
		if (StringUtils.isBlank(sessionId) || !sessionId.matches("\\w{24}")) {
			return "";
		}
		// session id
		SessionVo sessionVo = sessionService.get(sessionId);
		if (null == sessionVo) {
			logger.warn("[ERROR] getUuidBySessionId 无效的会话ID.");
			throw new DocServiceException("无效的会话ID！");
		}
		String uuid = sessionVo.getUuid();
		return uuid;
	}
	

	public Map<String, String> getRemoteAuthMap(HttpServletRequest request) {
		// default value
		Map<String, String> authMap = null;
		try {
			authMap = om.readValue(thdViewCheckDefault, new TypeReference<HashMap<String, String>>() { });
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn("[DEFAULT AHTU VALUE ERROR]" + e.getMessage());
		}
		if (StringUtils.isNotBlank(thdViewCheckUrl)) {
			String thdViewCheckKeyValue = request.getParameter(thdViewCheckKeyName);
			String checkUrl = thdViewCheckUrl + "?" + thdViewCheckKeyName + "=" + thdViewCheckKeyValue;
			try {
				String str = RemoteUtil.get(checkUrl);
				logger.info("[REMOTE GET] URL(" + checkUrl + "), RET(" + str + ")");
				Map<String, String> remoteMap = om.readValue(str, new TypeReference<HashMap<String, String>>() { });
				String remoteUpload = remoteMap.get("upload");
				String remoteView = remoteMap.get("view");
				String remoteRead = remoteMap.get("read");
				String remoteDown = remoteMap.get("down");
				String remoteCopy = remoteMap.get("copy");
				String remoteInfo = remoteMap.get("info");
				if (StringUtils.isNotBlank(remoteUpload) && remoteUpload.matches("\\d{1,}")) {
					authMap.put("upload", remoteUpload);
				}
				if (StringUtils.isNotBlank(remoteView) && remoteView.matches("\\d{1,}")) {
					authMap.put("view", remoteView);
				}
				if (StringUtils.isNotBlank(remoteRead) && remoteRead.matches("\\d{1,}")) {
					authMap.put("read", remoteRead);
				}
				if (StringUtils.isNotBlank(remoteDown) && remoteDown.matches("\\d{1,}")) {
					authMap.put("down", remoteDown);
				}
				if (StringUtils.isNotBlank(remoteCopy) && remoteCopy.matches("\\d{1,}")) {
					authMap.put("copy", remoteCopy);
				}
				if (StringUtils.isNotBlank(remoteInfo)) {
					authMap.put("info", remoteInfo);
				}
			} catch (Exception e) {
				logger.warn("[REMOTE GET] URL(" + checkUrl + "), EXCEPTION(" + e.getMessage() + ")");
			}
		}

		// admin login
		if (null != authMap && isAdminLogin(request)) {
			authMap.put("upload", "1");
			authMap.put("view", "1");
			authMap.put("read", "0");
			authMap.put("down", "1");
			authMap.put("copy", "1");
		}
		return authMap;
	}

	private boolean isAdminLogin(HttpServletRequest req) {
		boolean isAdmin = false;
		try {
			Cookie[] cookies = req.getCookies();
			String sid = null;
			if (null != cookies) {
				for (Cookie cookie : cookies) {
					if ("IDOCVSID".equalsIgnoreCase(cookie.getName())) {
						sid = cookie.getValue();
						break;
					}
				}
				UserVo vo = userService.getBySid(sid);
				if (100 == vo.getStatus()) {
					isAdmin = true;
				}
			}
		} catch (Exception e) {
			logger.warn("判断管理员登录失败: " + e.getMessage());
		}
		return isAdmin;
	}
}