package com.idocv.docview.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdUtil {
	
	public static String runLinux(String... cmd) {
		String result = "";
		StringBuffer sbOut = new StringBuffer();
		try {
			String line;
			String[] use = { "/bin/sh", "-c" };
			List<String> cmdList = new ArrayList<String>(Arrays.asList(use));
			cmdList.addAll(Arrays.asList(cmd));
			
			// ﻿Process p = Runtime.getRuntime().exec(cmdList.toArray(new String[0]));
			ProcessBuilder builder = new ProcessBuilder(cmdList.toArray(new String[0]));
			builder.redirectErrorStream(true);
			Process p = builder.start();
			
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
			while ((line = bri.readLine()) != null) {
				sbOut.append(line + "\n");
			}
			bri.close();
			Integer exitValue = p.waitFor();
			// System.out.println("Exit value: " + exitValue);
			if (0 == exitValue || 1 == exitValue) {
				// do something here...
			}
			result = "Exit value: " + exitValue + "\n\n" + "Output: \n" + sbOut.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String runWindows(String... cmd) {
		String result = "";
		StringBuffer sbOut = new StringBuffer();
		StringBuffer sbErr = new StringBuffer();
		try {
			String line;
			String[] use = { "cmd", "/c" };
			List<String> cmdList = new ArrayList<String>(Arrays.asList(use));
			cmdList.addAll(Arrays.asList(cmd));
			
			// ﻿Process p = Runtime.getRuntime().exec(cmdList.toArray(new String[0]));
			ProcessBuilder builder = new ProcessBuilder(cmdList.toArray(new String[0]));
			builder.redirectErrorStream(true);
			Process p = builder.start();
			
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
			while ((line = bri.readLine()) != null) {
				sbOut.append(line + "\n");
			}
			bri.close();
			Integer exitValue = p.waitFor();
			// System.out.println("Exit value: " + exitValue);
			if (0 == exitValue || 1 == exitValue) {
				// do something here...
			}
			result = "Exit value: " + exitValue + "\n\n" + "Output: \n" + sbOut.toString() + "\n\nError message: " + sbErr.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		System.out.println(runWindows("dir"));
	}
}
