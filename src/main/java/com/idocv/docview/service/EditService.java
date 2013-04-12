package com.idocv.docview.service;

import com.idocv.docview.exception.DocServiceException;

/**
 * Document Edit Service
 * 
 * @author Godwin
 * @since 2013-04-12
 * @version 1.0
 * 
 */
public interface EditService {

	/**
	 * Get the HTML body of a document
	 * 
	 * @param uuid
	 * @return
	 * @throws DocServiceException
	 */
	String getHtmlBody(String uuid) throws DocServiceException;

}