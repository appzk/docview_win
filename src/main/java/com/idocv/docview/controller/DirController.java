package com.idocv.docview.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.service.AppService;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.SessionService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;

@Controller
@RequestMapping("dir")
public class DirController {
	
	private static final Logger logger = LoggerFactory.getLogger(DirController.class);
	
	@Resource
	private AppService appService;

	@Resource
	private DocService docService;

	@Resource
	private ViewService viewService;

	@Resource
	private SessionService sessionService;

	private @Value("${thd.view.template}")
	String thdViewTemplate;

	private @Value("${view.page.load.async}")
	boolean pageLoadAsync;

	@Resource
	private RcUtil rcUtil;
	
	@RequestMapping("load")
	public String loadPage() {
		return "dir/load";
	}

	@ResponseBody
	@RequestMapping("load.json")
	public Map<String, String> loadDirJson(@RequestParam(value = "dir", required = true) String dirPath) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			File dir = new File(dirPath);
			Map<String, File> docMap = listDocs(dir);
			if (null == docMap || docMap.isEmpty()) {
				result.put("code", "0");
				result.put("msg", "There is NO DOCs in <" + dir.getAbsolutePath() + ">");
				return result;
			}
			if (docMap.size() > 1000) {
				result.put("code", "0");
				result.put("msg", "There are TOO many(over 1000) docs under <" + dir.getAbsolutePath() + ">");
				return result;
			}
			
			Map<String, File> newDocMap = new LinkedHashMap<String, File>();
			for (Entry<String, File> entry : docMap.entrySet()) {
				String fileMd5 = entry.getKey();
				File fileDoc = entry.getValue();
				DocVo docVo = docService.getByMd5(fileMd5);
				if (null == docVo || StringUtils.isBlank(docVo.getMd5())) {
					newDocMap.put(fileMd5, fileDoc);
					
					// add file to database
					byte[] data = FileUtils.readFileToByteArray(fileDoc);
					docService.add("test", "111111111111111111111111", fileDoc.getName(), data, 1, null);
				}
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append("<br />共找到" + docMap.size() + "个文档，其中" + newDocMap.size()
					+ "个新文档，详情如下：<br />");
			for (Entry<String, File> entry : newDocMap.entrySet()) {
				String fileMd5 = entry.getKey();
				File fileDoc = entry.getValue();
				sb.append(StringEscapeUtils.escapeHtml(fileMd5 + " - " + fileDoc.getAbsolutePath()) + "<br />");
			}
			result.put("code", "1");
			result.put("data", sb.toString());
		} catch (Exception e) {
			result.put("code", "0");
			result.put("msg", e.getMessage());
		}
		return result;
	}

	/**
	 * list docs under directory
	 * 
	 * @param file
	 * @return map<md5, path>
	 */
	public static Map<String, File> listDocs(File file) {
		Map<String, File> map = new LinkedHashMap<String, File>();
		if (null == file) {
			return map;
		}
		if (file.isDirectory()) {
			File[] subFiles = file.listFiles();
			if (null == subFiles || subFiles.length == 0) {
				return map;
			}
			for (File subFile : subFiles) {
				map.putAll(listDocs(subFile));
			}
			return map;
		} else if (file.isFile()) {
			try {
				String lowerFileName = file.getName().toLowerCase();
				if (!lowerFileName.matches(".*?\\.(doc|docx|xls|xlsx|ppt|pptx|pdf|txt)")) {
					return map;
				}
				byte[] data = FileUtils.readFileToByteArray(file);
				String md5 = DigestUtils.md5Hex(data);
				map.put(md5, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return map;
		} else {
			return map;
		}
	}

	public static void main(String[] args) {
		File dir = new File("D:\\360云盘\\zzz\\idocv\\res");
		Map<String, File> map = listDocs(dir);
		System.out.println("Files:");
		for (Entry<String, File> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " - " + entry.getValue());
		}
		System.out.println("Done!");
	}
}