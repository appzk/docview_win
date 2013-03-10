package com.idocv.docview.dao;

import java.util.List;

import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.DocPo;

public interface DocDao {

	void add(String id, String uuid, String appId, String name, long size, String ext) throws DBException;

	/**
	 * Save a Document.
	 * 
	 * @param doc
	 */
	void add(DocPo doc) throws DBException;

	/**
	 * Delete a document.
	 */
	boolean delete(String uuid) throws DBException;

	/**
	 * Update Url.
	 * 
	 * @param uuid
	 * @param url
	 * @return
	 */
	boolean updateUrl(String uuid, String url) throws DBException;

	/**
	 * log preview with time stamp
	 * 
	 * @param uuid
	 * @throws DBException
	 */
	public void logView(String uuid) throws DBException;

	/**
	 * log download with time stamp
	 * 
	 * @param uuid
	 * @throws DBException
	 */
	public void logDownload(String uuid) throws DBException;

	/**
	 * Change access mode
	 * 
	 * @param uuid
	 * @param mode
	 * @throws DBException
	 */
	public void updateMode(String uuid, int mode) throws DBException;

	/**
	 * get Doc by rid.
	 * 
	 * @param rid
	 * @param whether include deleted doc
	 * @return
	 */
	DocPo get(String rid, boolean includeDeleted) throws DBException;
	
	/**
	 * get Doc by UUID.
	 * 
	 * @param getByUuid
	 * @param whether include deleted doc
	 * @return
	 */
	DocPo getByUuid(String uuid, boolean includeDeleted) throws DBException;
	
	/**
	 * get Doc by URL.
	 * 
	 * @param url
	 * @return
	 */
	DocPo getUrl(String url) throws DBException;

	/**
	 * get Doc list by rid array.
	 * 
	 * @param rids
	 * @return
	 */
	List<DocPo> list(int offset, int limit) throws DBException;

	/**
	 * Get document count.
	 * 
	 * @param includeDeleted
	 * @return
	 * @throws DBException
	 */
	public long count(boolean includeDeleted) throws DBException;
}
