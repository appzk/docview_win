package com.idocv.docview.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.ThdService;
import com.idocv.docview.util.CmdUtil;

@Service
public class ThdServiceImpl implements ThdService {

	private static final Logger logger = LoggerFactory.getLogger(ThdServiceImpl.class);
	
	private @Value("${thd.upload.check.switch}")
	boolean thdUploadCheckSwitch = false;

	private @Value("${thd.upload.check.key}")
	String thdUploadCheckKey;

	@Override
	public boolean validateUser(String uid, String tid, String sid) throws DocServiceException {
		String uidTid = uid + tid;
		if (StringUtils.isBlank(uid) || StringUtils.isBlank(tid)
				|| StringUtils.isBlank(sid)
				|| StringUtils.isBlank(thdUploadCheckKey)
				|| uidTid.length() < thdUploadCheckKey.length()
				|| uidTid.length() >= 4096) {
			return false;
		}
		byte[] xorStr = new byte[uidTid.length()];
		byte securityKey[] = thdUploadCheckKey.getBytes();
		byte toXorByte[] = uidTid.getBytes();
		for (int i = 0, j = 0; i < uidTid.length(); i++, ++j) {
			if (j >= thdUploadCheckKey.length()) {
				j = 0;
			}
			xorStr[i] = (byte) (securityKey[j] ^ toXorByte[i]);
		}
		String resSid = DigestUtils.md5Hex(xorStr);
		if (!resSid.equals(sid)) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		String checker = "d:/UploadCheck.jar";
		String result = CmdUtil.runWindows("java", "-jar", checker, "uid", "tid", "sid");
		if (StringUtils.isNotBlank(result) && result.matches("(?s).*?:(\\w{1,}).*")) {
			String validCode = result.replaceFirst("(?s).*?:(\\w{1,}).*", "$1");
			System.out.println("validCode<" + validCode + ">");
			if ("1".equalsIgnoreCase(validCode)) {
				System.out.println("valid...");
			}
		}
	}
}