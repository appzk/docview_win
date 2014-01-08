package com.idocv.docview.service;

import java.io.File;

import com.idocv.docview.exception.DocServiceException;

public interface ThdService {

	/**
	 * Check user, valid returns true, invalid throws Exception
	 * 
	 * @param uid
	 * @param tid
	 * @param sid
	 * @return
	 * @throws DocServiceException
	 */
	boolean validateUser(String uid, String tid, String sid) throws DocServiceException;

	/**
	 * Get Third Party specific file MD5, throws Exception if failed
	 * 
	 * @param src
	 * @return
	 * @throws DocServiceException
	 */
	String getFileMd5(File src) throws DocServiceException;
}