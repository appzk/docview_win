package com.idocv.docview.demo;

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
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.util.EncodingUtil;

public class UploadDemo {
	public static void main(String[] args) {
		String serverUrl = "http://api.idocv.com/doc/upload";
		File file = new File("e:/测试文档.docx");
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", "testtoken");
		String uploadResult = uploadFile(serverUrl, file, params);
		System.out.println("uploadResult: " + uploadResult);
	}
	
	public static String uploadFile(String serverUrl, File file, Map<String, String> params) {
		try {
			HttpClient client = new HttpClient();
			PostMethod filePost = new PostMethod(serverUrl);
			filePost.getParams().setContentCharset("UTF-8");
			List<Part> partList = new ArrayList<Part>();
			if (null != params && !params.isEmpty()) {
				for (Entry<String, String> entry : params.entrySet()) {
					partList.add(new StringPart(entry.getKey(), entry.getValue()));
				}
			}
			
			FilePart filePart = new FilePart(file.getName(), file) {
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
			};
			partList.add(filePart);
			filePost.setRequestEntity(new MultipartRequestEntity(partList.toArray(new Part[0]), filePost.getParams()));
			int status = client.executeMethod(filePost);
			String responseBody = filePost.getResponseBodyAsString();
			return responseBody;
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
}