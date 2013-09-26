package com.idocv.dir;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class DeleteDirectory {
	public static void main(String[] args) {
		File dir = new File("D:/idocv/data/test/2013");
		System.out.println(dir.isDirectory());
		listFiles(dir);
	}

	public static void listFiles(File file) {
		if (null == file) {
			return;
		}
		if (file.isDirectory()) {
			if (file.getName().matches("\\d{6}_\\d+?_\\w{5,}?p")) {
				System.out.println("Deleteing " + file.getAbsolutePath());
				try {
					FileUtils.deleteDirectory(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			File[] subFiles = file.listFiles();
			if (null == subFiles || subFiles.length == 0) {
				return;
			}
			for (File subFile : subFiles) {
				listFiles(subFile);
			}
		}
	}
}
