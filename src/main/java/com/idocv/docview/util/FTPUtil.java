package com.idocv.docview.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUtil {

	/**
	 * get fileName from URL
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileNameFromUrl(String url) {
		try {
			String fileName = new URL(url).getPath();
			if (null != fileName && fileName.contains("/")) {
				fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
			}
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * download file from FTP URL
	 * 
	 * @param url
	 * @param user
	 * @param pass
	 * @return
	 */
	public static byte[] downloadFTPFile(String url, String user, String pass) {
		/** 本地字符编码 */
		String LOCAL_CHARSET = "GBK";
		// FTP协议里面，规定文件名编码为iso-8859-1
		String SERVER_CHARSET = "ISO-8859-1";
		FTPClient ftpClient = new FTPClient();
		byte[] data = null;
		try {
			URL ftpUrlObj = new URL(url);
			String server = ftpUrlObj.getHost();
			String remoteFile = ftpUrlObj.getFile();
			int port = 21;
			ftpClient.connect(server, port);
			user = StringUtils.isBlank(user) ? "anonymous" : user;
			if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				if (ftpClient.login(user, pass)) {
					if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
						LOCAL_CHARSET = "UTF-8";
					}
					ftpClient.setControlEncoding(LOCAL_CHARSET);
					ftpClient.enterLocalPassiveMode();// 设置被动模式
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// 设置传输的模式
					System.out.println("FTP remoteFile before encodeing: " + remoteFile);
					remoteFile = new String(remoteFile.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
					System.out.println("FTP remoteFile after encodeing: " + remoteFile);
					InputStream inputStream = ftpClient.retrieveFileStream(remoteFile);
					data = IOUtils.toByteArray(inputStream);
					boolean success = ftpClient.completePendingCommand();
					if (success) {
						System.out.println("FTP file <" + remoteFile + "> has been downloaded successfully.");
					}
					inputStream.close();
				} else {
					throw new Exception("无法连接到FTP服务器，请检查用户名密码是否正确！");
				}
			}
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return data;
	}

}