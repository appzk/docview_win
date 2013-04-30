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
	 * @paramr appId
	 * @param name
	 * @param data
	 * @param mode 0-private, 1-public
	 * @return
	 */
	DocVo addByApp(String appId, String name, byte[] data, int mode) throws DocServiceException;
	
	DocVo addByUser(String sid, String name, byte[] data, int mode, String labelName) throws DocServiceException;

	/**
	 * Save URL resource to local directory.
	 * 
	 * @param url
	 * @param name
	 * @param mode 0-private, 1-public
	 * @return
	 */
	DocVo addUrl(String token, String url, String name, int mode) throws DocServiceException;

	boolean delete(String uuid) throws DocServiceException;

	public void logView(String uuid) throws DocServiceException;

	public void logDownload(String uuid) throws DocServiceException;

	public void updateMode(String token, String uuid, int mode) throws DocServiceException;

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

	/**
	 * 文档列表
	 * 	1. sid存在（已登录）
	 * 		a. 普通用户：列出对应用户文档（包括私有文档）
	 * 		b. 应用管理员用户：列出对应应用文档（包括私有文档）
	 * 	2. sid不存在（未登录）等有app名称
	 * 		列出对应app公开文档
	 */
	Paging<DocVo> list(String app, String sid, int start, int length, String search, String label, QueryOrder queryOrder) throws DocServiceException;

	long count(boolean includeDeleted) throws DocServiceException;
}
