package com.idocv.docview.service;

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
}