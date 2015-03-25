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

	void save(String uuid, String body) throws DocServiceException;

	/**
	 * Get latest version number
	 * 
	 * @param uuid
	 * @return
	 * @throws DocServiceException
	 */
	int getLatestVersion(String uuid) throws DocServiceException;

	/**
	 * Get BODY string by version
	 * 
	 * @param uuid
	 * @param version -1: latest version, 0: original doc, 1-n: version n
	 * @return
	 * @throws DocServiceException
	 */
	String getBody(String uuid, int version) throws DocServiceException;

	/**
	 * Get the HTML body of a document
	 * 
	 * @param uuid
	 * @return
	 * @throws DocServiceException
	 */
	String getHtmlBody(String uuid) throws DocServiceException;

}