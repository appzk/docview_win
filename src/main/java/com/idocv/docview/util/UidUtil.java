package com.idocv.docview.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class UidUtil {
	public static String getUid(String sid) {
		if (sid == null || sid.length() <= 32) {
			return null;
		} else {
			try {
				String _sid = new String(Base64.decodeBase64(sid.getBytes("UTF-8")), "UTF-8");
				String[] u = _sid.split("\\.");
				if (u.length != 4) {
					return null;
				} else {
					return u[0].trim();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static String getSid(String uid, String UA) {
		StringBuilder sb = new StringBuilder().append(uid).append(".").append(UA).append(".").append(System.currentTimeMillis());
		sb.reverse();
		String key = DigestUtils.md5Hex(sb.toString());
		sb.reverse();
		String t = sb.append(".").append(key).toString();
		String sid = "";
		try {
			sid = Base64.encodeBase64URLSafeString(t.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sid;
	}

	public static void main(String[] args) {
		String uid = "12345678901234";
		String sid = getSid(uid, "UA");
		System.out.println("sid: " + sid);

		System.out.println("uid: " + getUid(sid));

		System.out.println(DigestUtils.md5Hex(uid));
	}
}