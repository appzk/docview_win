package com.idocv.docview.controller;

import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("info")
public class InfoController {

	@ResponseBody
	@RequestMapping("disk")
	public Map<String, String> disk() {
		Map<String, String> diskInfo = new HashMap<String, String>();
		diskInfo.putAll(getDiskInfo());
		return diskInfo;
	}

	private static Map<String, String> getDiskInfo() {
		Map<String, String> diskInfo = new HashMap<String, String>();
		NumberFormat nf = NumberFormat.getNumberInstance();
		for (Path root : FileSystems.getDefault().getRootDirectories()) {
			try {
				FileStore store = Files.getFileStore(root);
				long usableSpace = store.getUsableSpace();
				long totalSpace = store.getTotalSpace();
				diskInfo.put(root + "-available", nf.format(usableSpace));
				diskInfo.put(root + "-total", nf.format(totalSpace));
				diskInfo.put(root + "-useRatio",
						new DecimalFormat("#.00")
								.format(((float) totalSpace - usableSpace)
										/ totalSpace));
			} catch (Exception e) {
				System.out.println("error querying space: " + e.toString());
			}
		}
		return diskInfo;
	}
}