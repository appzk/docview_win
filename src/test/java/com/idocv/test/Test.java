package com.idocv.test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
	public static void main(String[] args) {
		String time = "2014-02-17 04:57:07";

		String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		System.out.println("cur:" + currentDate);
		System.out.println(time.startsWith(currentDate));
	}
}