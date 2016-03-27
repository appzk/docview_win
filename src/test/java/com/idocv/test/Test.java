package com.idocv.test;

import java.io.File;

public class Test {
	public static void main(String[] args) {
		try {
			File file = new File("test.docx");
			System.out.println(file.getParent());
			System.out.println(file.getPath());
			System.out.println(file.getAbsolutePath());
			System.out.println(file.getAbsoluteFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}