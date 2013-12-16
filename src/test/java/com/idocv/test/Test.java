package com.idocv.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class Test {
	public static void main(String[] args) {
		try {
			Path path = Paths.get("d:/a.pdf");
			BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
			FileTime ftCreate = attrs.creationTime();
			FileTime ftLastModified = attrs.lastModifiedTime();
			long diffTime = ftCreate.toMillis() - ftLastModified.toMillis();
			diffTime = diffTime > 0 ? diffTime : -diffTime;
			if (diffTime > 5000) {
				System.out.println("Modified!");
			}
			System.out.println("ftCreate: " + ftCreate.toMillis());
			System.out.println("ftLastModified: " + ftLastModified.toMillis());
			System.out.println("M: " + diffTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getHost(String url) {
		return url.replaceFirst("((http[s]?)?(://))?([^/]*)(/?.*)", "$4");
	}
}
