package com.idocv.test;

import org.apache.commons.lang.StringEscapeUtils;

public class Test {
	public static void main(String[] args) {
		String s = "<a href=\"http://www.idoc.vom\">I Doc View</a>abcd\nefg";
		String es = StringEscapeUtils.escapeHtml(s).replaceAll("(\r)?\n", "<br />");
		System.err.println(es);
	}
}
