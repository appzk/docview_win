package com.idocv.docview;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class Test {
	public static void main(String[] args) {
		File dir = new File("/Users/Godwin/tmp/docview/ppttest2");
		File[] files = dir.listFiles();

		Arrays.sort(files, new FileComparator());
		
		for (File file : files) {
			System.out.println(file.getAbsolutePath());
		}

		System.out.println("ab12.jpg".replaceFirst("[^\\d]*?(\\d+).*", "$1"));
	}
	
	static class FileComparator implements Comparator<File> {

		@Override
		public int compare(File o1, File o2) {
			String name1 = o1.getName();
			String name2 = o2.getName();
			String nameRegex = "[^\\d]*?(\\d+).*";
			int nameDigit1;
			int nameDigit2;
			try {
				nameDigit1 = Integer.valueOf(name1.replaceFirst(nameRegex, "$1"));
				nameDigit2 = Integer.valueOf(name2.replaceFirst(nameRegex, "$1"));
			} catch (NumberFormatException e) {
				return -1;
			}
			return nameDigit1 == nameDigit2 ? 0 : (nameDigit1 > nameDigit2 ? 1 : -1);
		}

	}
}