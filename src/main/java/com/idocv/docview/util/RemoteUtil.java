package com.idocv.docview.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RemoteUtil {

	public static String get(String url) {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		method.getParams().setContentCharset("UTF-8");
		method.setRequestHeader("Content-Type", "text/html; charset=UTF-8");
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				return "ERROR:status_code" + statusCode;
			}
			String response = method.getResponseBodyAsString();
			// List<HashMap<String, String>> authMap = om.readValue(response, new TypeReference<List<HashMap<String, String>>>() { });
			return response;
		} catch (Exception e) {
			System.out.println("[ERROR] Get expire status error(" + e.getMessage() + ")");
			return "ERROR:" + e.getMessage();
		} finally {
			method.releaseConnection();
		}
	}

	public static void main(String[] args) {
		String url = "http://data.idocv.com/check.json";
		String str = get(url);
		System.out.println(str);
		System.out.println((int) str.toCharArray()[0]);

		try {
			Map<String, String> map = new ObjectMapper().readValue(str,
					new TypeReference<HashMap<String, String>>() {
					});
			System.out.println("map: " + map);
			String read = map.get("read");
			System.out.println("1".equals(read));

			String copy = map.get("copy");
			System.out.println("1".equals(copy));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}