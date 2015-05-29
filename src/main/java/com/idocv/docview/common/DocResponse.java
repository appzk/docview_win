package com.idocv.docview.common;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DocResponse<T> {

	private int code = 0;
	private String desc;
	private T data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public DocResponse getSuccessResponse(T data) {
		this.setCode(1);
		this.setDesc("success");
		this.setData(data);
		return this;
	}

	public static Map<String, Object> getSuccessResponseMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "1");
		map.put("desc", "success");
		return map;
	}

	public static Map<String, Object> getSuccessResponseMap(Object data) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "1");
		map.put("desc", "success");
		if (null != data) {
			map.put("data", data);
		}
		return map;
	}

	public DocResponse getFailResponse(int code, String desc) {
		this.setCode(code);
		this.setDesc(desc);
		return this;
	}

	public static Map<String, Object> getErrorResponseMap(String desc) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "0");
		map.put("desc", desc);
		return map;
	}

	/**
	 * 设置下载文件名
	 * 
	 * @param req
	 * @param resp
	 * @param filename
	 * @param resetHeader
	 */
	public static void setResponseHeaders(HttpServletRequest req, HttpServletResponse resp, String filename) {
		try {
			String agent = req.getHeader("USER-AGENT").toLowerCase();
			
			// 设置response的Header
			if (agent.indexOf("msie") != -1) {
				resp.addHeader("Content-Disposition", "attachment;filename="
						+ new String(filename.getBytes("gbk"), "iso-8859-1"));
			} else if (agent.indexOf("firefox") != -1
					|| agent.indexOf("chrome") != -1) {
				resp.setHeader("filename", filename);
				resp.addHeader("Content-disposition", "attachment; filename="
						+ "\""
						+ new String(filename.getBytes("UTF-8"), "ISO8859-1")
						+ "\"");

			} else if (agent.indexOf("safari") != -1) {
				resp.addHeader("Content-Disposition", "attachment;filename="
						+ new String(filename.getBytes("utf-8"), "iso8859-1"));
			} else if (agent.indexOf("opera") != -1) {
				resp.addHeader("Content-Disposition", "attachment;filename="
						+ new String(filename.getBytes("utf-8"), "iso8859-1"));
			} else {
				resp.addHeader("Content-Disposition", "attachment;filename="
						+ new String(filename.getBytes("gbk"), "iso-8859-1"));
			}
			resp.setContentType("application/octet-stream");
		} catch (UnsupportedEncodingException e) {

		}
	}
}
