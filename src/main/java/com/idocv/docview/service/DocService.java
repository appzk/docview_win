package com.idocv.docview.service;

import com.idocv.docview.common.Paging;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.DocPo;

/**
 * Document Service
 * 
 * @author Godwin
 * @since 2012-10-22
 * @version 1.0
 * 
 */
public interface DocService {

	/**
	 * Save normal resource to local directory.
	 * 
	 * @paramr appId
	 * @param name
	 * @param data
	 * @return
	 */
	DocPo add(String appId, String name, byte[] data) throws DocServiceException;

	/**
	 * Save URL resource to local directory.
	 * 
	 * @param url
	 * @param name
	 * @return
	 */
	DocPo addUrl(String ip, String url, String name) throws DocServiceException;

	boolean delete(String rid) throws DocServiceException;

	/**
	 * Get DocPo from local database, if NULL, get it from remote RC server.
	 * 
	 * @param rid
	 * @return
	 */
	DocPo get(String rid) throws DocServiceException;

	/**
	 * Get DocPo from local database by URL.
	 * 
	 * @param url
	 * @return
	 */
	DocPo getUrl(String url) throws DocServiceException;

	Paging<DocPo> list(int start, int length) throws DocServiceException;

	long count(boolean includeDeleted) throws DocServiceException;
}
