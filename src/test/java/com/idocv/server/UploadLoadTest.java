package com.idocv.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UploadLoadTest {

	public static void main(String[] args) {
		try {
			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("mode", "1");
			params.put("uid", "12345678");
			params.put("appid", "abc");
			params.put("tid", "1387900884");
			params.put("sid", "7b62002d461dfc5b31b35a3595e14f78");
			params.put("token", "testtoken");
			File file = new File("d:/test.docx");
			final String name = file.getName();
			final byte[] data = FileUtils.readFileToByteArray(file);
			System.out.println("name: " + name + ", length: " + data.length);
			// upload2Remote(name, data, params);
			final List<String> success = new ArrayList<String>();
			final List<String> error = new ArrayList<String>();

			List<Thread> threadList = new ArrayList<Thread>();

			for (int i = 0; i < 70; i++) {
				Thread t = new Thread() {
					public void run() {
						if (upload2Remote(name, data, params)) {
							success.add(Thread.currentThread().getName());
						} else {
							success.add(Thread.currentThread().getName());
						}
					};
				};
				t.start();
				threadList.add(t);
			}

			for (Thread t : threadList) {
				t.join();
			}

			System.out.println("success(" + success.size() + "): " + success);
			System.out.println("error(" + error.size() + "): " + error);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean upload2Remote(String fileName, byte[] bytes, Map<String, Object> params) {
		try {
			String url = "http://api.idocv.com/xiwang/upload";
			// String url = "http://api.idocv.com/doc/upload";
			HttpClient client = new HttpClient();
			StringBuffer paramString = new StringBuffer();
			if (null != params && !params.isEmpty()) {
				for (Entry<String, Object> entry : params.entrySet()) {
					if (paramString.length() > 0) {
						paramString.append("&");
					}
					paramString.append(entry.getKey() + "=" + entry.getValue());
				}
				url = url + "?" + paramString;
			}
			PostMethod filePost = new PostMethod(url);
			Part[] parts = { new FilePart("file", new ByteArrayPartSource(fileName, bytes)) {

				@Override
				protected void sendDispositionHeader(OutputStream out) throws IOException {
					super.sendDispositionHeader(out);
					String filename = getSource().getFileName();
					if (filename != null) {
						out.write(EncodingUtil.getAsciiBytes(FILE_NAME));
						out.write(QUOTE_BYTES);
						out.write(EncodingUtil.getBytes(filename, "utf-8"));
						out.write(QUOTE_BYTES);
					}
				}

			} };
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			int status = client.executeMethod(filePost);
			String result = filePost.getResponseBodyAsString();
			// info(" upload result: " + result);
			// return true;
			if (200 == status && "0".equalsIgnoreCase(new ObjectMapper().readTree(result).get("ret").toString())) {
				// System.out.println("Upload success, result: " + result);
				info("upload to remote success: " + result + ", params=" + params);
				return true;
			}
			error("[CLUSTER] upload to remote error with result: " + result + ", params=" + params);
		} catch (Exception e) {
			error("[CLUSTER] upload to remote error: " + e.getMessage() + ", params=" + params);
		}
		return false;
	}

	public static void error(String msg) {
		System.err.println("[ERROR] " + msg);
	}

	public static void info(String msg) {
		System.out.println("[INFO] " + msg);
	}
}