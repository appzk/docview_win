package com.idocv.docview.service;

import com.idocv.docview.common.Paging;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.vo.DocVo;

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
	DocVo add(String appId, String name, byte[] data) throws DocServiceException;

	/**
	 * Save URL resource to local directory.
	 * 
	 * @param url
	 * @param name
	 * @return
	 */
	DocVo addUrl(String appKey, String url, String name) throws DocServiceException;

	boolean delete(String uuid) throws DocServiceException;

	public void logView(String uuid) throws DocServiceException;

	public void logDownload(String uuid) throws DocServiceException;

	/**
	 * Get DocPo from local database, if NULL, get it from remote RC server.
	 * 
	 * @param rid
	 * @return
	 */
	DocVo get(String rid) throws DocServiceException;
	
	/**
	 * Get DocPo by UUID from local database.
	 * 
	 * @param uuid
	 * @return
	 */
	DocVo getByUuid(String uuid) throws DocServiceException;

	/**
	 * Get DocPo from local database by URL.
	 * 
	 * @param url
	 * @return
	 */
	DocVo getUrl(String url) throws DocServiceException;

	Paging<DocVo> list(int start, int length) throws DocServiceException;

	long count(boolean includeDeleted) throws DocServiceException;
}
