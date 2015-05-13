package com.idocv.docview.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ConfInterceptor extends HandlerInterceptorAdapter {

	@Value("${docview.version}")
	private String version;

	@Value("${view.page.ppt.draw.server}")
	private String confDrawServer;

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView model) throws Exception {
		if (null != model) {
			request.setAttribute("version", version);
			request.setAttribute("confDrawServer", confDrawServer);

			// Do NOT use this way, or it will be exposed as URL parameter.
			// model.addObject("version", version);
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}