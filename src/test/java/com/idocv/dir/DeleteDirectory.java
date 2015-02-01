package com.idocv.dir;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.idocv.docview.common.ViewType;

public class DeleteDirectory {

	private static final ViewType DELETE_TYPE = ViewType.WORD;// delete type.

	public static void main(String[] args) {
		File dir = new File("D:/idocv/data/test/2013");
		System.out.println(dir.getAbsolutePath() + " is a directory: " + dir.isDirectory());
		listAndDeleteDirectory(dir);
		System.out.println("Done!");
	}

	public static void listAndDeleteDirectory(File file) {
		if (null == file) {
			return;
		}
		if (file.isDirectory()) {
			if (file.getName().matches("\\d{6}_\\d+?_\\w{5,}?" + DELETE_TYPE.getSymbol())) {
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
				listAndDeleteDirectory(subFile);
			}
		}
	}
}
