package com.idocv.docview;

import com.idocv.docview.util.IdUtil;

public class Test {
	public static void main(String[] args) {
		String id = IdUtil.getObjectId();
		System.out.println(id.matches("\\w{24}"));
	}
}