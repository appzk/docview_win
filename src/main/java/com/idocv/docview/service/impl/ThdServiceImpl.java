package com.idocv.docview.service.impl;

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

	private @Value("${thd.upload.checker}")
	String thdUploadChecker;

	private @Value("${thd.upload.file.md5}")
	String thdUploadFileMd5;

	@Override
	public boolean validateUser(String uid, String tid, String sid) throws DocServiceException {
		if (!thdUploadCheckSwitch) {
			return true;
		}
		String result = CmdUtil.runWindows("java", "-jar", thdUploadChecker, uid, tid, sid);
		if (StringUtils.isNotBlank(result) && result.matches("(?s).*?:(\\w{1,}).*")) {
			String validCode = result.replaceFirst("(?s).*?:(\\w{1,}).*", "$1");
			if ("1".equalsIgnoreCase(validCode)) {
				return true;
			}
		}
		logger.error("[CLUSTER] 验证用户失败，uid=" + uid + ", tid=" + tid + ", sid="
				+ sid + ", return=" + result);
		throw new DocServiceException("用户验证失败！");
	}

	@Override
	public String getFileMd5(String src) throws DocServiceException {
		String result = CmdUtil.runWindows("java", "-jar", thdUploadFileMd5, src);
		if (StringUtils.isNotBlank(result) && result.matches("(?s).*?:(\\w{32}).*")) {
			String md5 = result.replaceFirst("(?s).*?:(\\w{32}).*", "$1");
			return md5;
		}
		logger.error("[CLUSTER] 获取文件MD5失败：src=" + src + ", return=" + result);
		throw new DocServiceException("获取文件MD5失败！");
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