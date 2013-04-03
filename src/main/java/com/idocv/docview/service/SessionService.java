package com.idocv.docview.service;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.vo.SessionVo;

/**
 * Session Service
 * 
 * @author Godwin
 * @since 2013-03-10
 * @version 1.0
 * 
 */
public interface SessionService {

	/**
	 * generate session id
	 * 
	 * @param appKey
	 * @param uuid
	 * @return
	 * @throws DocServiceException
	 */
	String add(String appKey, String uuid) throws DocServiceException;

	SessionVo get(String id) throws DocServiceException;
}