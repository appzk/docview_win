package com.idocv.test;

public class Test {
	public static void main(String[] args) {
		String s = "jpg,gif,png,bmp@image#mp3,midi@audio#avi,rmvb,mp4,mkv@video";
		String ext = "bmp";
		System.out.println(s.replaceFirst(".*?" + ext + ".*?@(\\w+).*", "$1"));
	}
}