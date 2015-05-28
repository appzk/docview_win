package com.idocv.docview.service;

import com.idocv.docview.common.Paging;
import com.idocv.docview.dao.DocDao.QueryOrder;
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
	 * @param app
	 * @param uid
	 * @param name
	 * @param data
	 * @param mode
	 * @param labelName
	 * @return
	 * @throws DocServiceException
	 */
	DocVo add(String app, String uid, String name, byte[] data, int mode, String labelName) throws DocServiceException;
	
	DocVo addUrl(String app, String uid, String name, String url, int mode, String labelName) throws DocServiceException;

	/**
	 * Delete file logically
	 * 
	 * @param token
	 * @param uuid
	 * @return
	 * @throws DocServiceException
	 */
	boolean delete(String token, String uuid) throws DocServiceException;

	/**
	 * Delete file
	 * 
	 * @param token
	 * @param uuid
	 * @param isPhysical
	 *            whether delete physical files.
	 * @return
	 * @throws DocServiceException
	 */
	boolean delete(String token, String uuid, boolean isPhysical) throws DocServiceException;

	public void logView(String uuid) throws DocServiceException;

	public void logDownload(String uuid) throws DocServiceException;

	public void updateMode(String token, String uuid, int mode) throws DocServiceException;
	
	public void resetConvert(String token, String uuid) throws DocServiceException;

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
	 * Get DocPo by MD5 from local database.
	 * 
	 * @param md5
	 * @return
	 */
	DocVo getByMd5(String md5) throws DocServiceException;

	/**
	 * Get DocPo from local database by URL.
	 * 
	 * @param url
	 * @return
	 */
	DocVo getUrl(String url) throws DocServiceException;

	/**
	 * 文档列表 1. sid存在（已登录） a. 普通用户：列出对应用户文档（包括私有文档） b. 应用管理员用户：列出对应应用文档（包括私有文档）
	 * 2. sid不存在（未登录）等有app名称 列出对应app公开文档
	 */
	Paging<DocVo> list(String app, String sid, int start, int length, String search, String label, QueryOrder queryOrder) throws DocServiceException;
	
	Paging<DocVo> listShare(String app, int start, int length, String search, String label, QueryOrder queryOrder) throws DocServiceException;

	long count(boolean includeDeleted) throws DocServiceException;
}
