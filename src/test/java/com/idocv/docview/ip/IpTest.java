package com.idocv.docview.ip;

import com.idocv.docview.util.IpUtil;


public class IpTest {
	public static void main(String[] args) {
		String encodedIp = "2qhl1c1g";
		String ip = IpUtil.decodeIp(encodedIp);
		System.out.println(ip);
	}
}
