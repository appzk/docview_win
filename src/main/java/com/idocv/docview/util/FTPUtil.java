package com.idocv.docview.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

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
		FTPClient ftpClient = new FTPClient();
		byte[] data = null;
		try {
			URL ftpUrlObj = new URL(url);
			String server = ftpUrlObj.getHost();
			String remoteFile = ftpUrlObj.getFile();
			int port = 21;
			ftpClient.connect(server, port);
			user = StringUtils.isBlank(user) ? "anonymous" : user;
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			InputStream inputStream = ftpClient.retrieveFileStream(remoteFile);
			data = IOUtils.toByteArray(inputStream);
			boolean success = ftpClient.completePendingCommand();
			if (success) {
				System.out.println("FTP file <" + remoteFile
						+ "> has been downloaded successfully.");
			}
			inputStream.close();
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