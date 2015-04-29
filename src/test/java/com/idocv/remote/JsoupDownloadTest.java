package com.idocv.remote;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.idocv.docview.service.impl.DocServiceImpl;
import com.idocv.docview.util.UrlUtil;

public class JsoupDownloadTest {
	
	public static void main(String[] args) {
		String url = "http://eipt.pousheng.com:8080/documents/10182/37567/%E6%B2%83%E5%A7%86%E9%85%92%E5%BA%97%E8%BF%9E%E9%94%81/7de903bc-70d4-4951-8b93-0958e0f5707b";
		downloadByJsoup(url);
	}

	public static void downloadByJsoup(String url) {
		String name = "";
		File destDir = new File("d:/tmp");
		
		// Web File
		Response urlResponse = null;
		try {
			String encodedUrl = UrlUtil.encodeUrl(url);
			encodedUrl = encodedUrl.replaceAll(" ", "%20");
			encodedUrl = encodedUrl.contains("://") ? encodedUrl : ("http://" + encodedUrl);
			if (encodedUrl.contains("https://")) {
				DocServiceImpl.setTrustAllCerts();
			}
			// urlResponse = Jsoup.connect(url).referrer(host).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreContentType(true).execute();
			urlResponse = Jsoup.connect(encodedUrl).timeout(60000).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreHttpErrors(true).followRedirects(true).ignoreContentType(true).execute();
			if (urlResponse.statusCode() == 307) {
				String sNewUrl = urlResponse.header("Location");
				if (sNewUrl != null && sNewUrl.length() > 7) {
					url = sNewUrl;
				}
				urlResponse = Jsoup.connect(encodedUrl).timeout(5000).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreHttpErrors(true).followRedirects(true).ignoreContentType(true).execute();
			}
			if (null == urlResponse) {
				throw new Exception("获取资源(" + url + ")时返回为空！");
			}
			
			// 获取文件名，优先级：1. 获取直接传入的name参数；2. 获取header里的filename参数；3. 根据url获取文件名
			String disposition = urlResponse.header("Content-Disposition");
			if (StringUtils.isBlank(name) && StringUtils.isNotBlank(disposition)) {
				disposition = new String(disposition.getBytes("ISO-8859-1"), "UTF-8");
				if (disposition.matches(".*?filename(\\*)?=(\"|.{1,15}?'')([^\"]*).*")) {
					name = disposition.replaceFirst(".*?filename(\\*)?=(\"|.{1,15}?'')([^\"]*).*", "$3");
					try {
						name = URLDecoder.decode(name, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			if (StringUtils.isBlank(name) && url.contains(".") && url.matches(".*/([^/]+\\.\\w{1,6})")) {
				name = url.replaceFirst(".*/([^/]+\\.\\w{1,6})", "$1");
			}
			
			byte[] data = urlResponse.bodyAsBytes();

			if (!destDir.isDirectory()) {
				destDir.mkdirs();
			}
			File destFile = new File(destDir, name);
			FileUtils.writeByteArrayToFile(destFile, data);
			System.out.println("Done!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}