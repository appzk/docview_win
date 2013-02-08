package com.idocv.docview.dao;

import java.util.List;

import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.DocPo;

public interface DocDao {

	void add(String id, String appId, String name, long size, String ext) throws DBException;

	/**
	 * Save a Document.
	 * 
	 * @param doc
	 */
	void add(DocPo doc) throws DBException;

	/**
	 * Delete a document.
	 */
	boolean delete(String rid) throws DBException;

	/**
	 * Update Url.
	 * 
	 * @param rid
	 * @param url
	 * @return
	 */
	boolean updateUrl(String rid, String url) throws DBException;

	/**
	 * get Doc by rid.
	 * 
	 * @param rid
	 * @return
	 */
	DocPo get(String rid) throws DBException;
	
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
