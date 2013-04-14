package com.idocv.docview.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.DocPo;

public interface DocDao {

	public enum QueryOrder {
		
		ASC("asc"), DESC("desc");

		private String field;
		private String direction;
		
		private QueryOrder(String direction) {
			this.direction = direction;
		}

		public static QueryOrder getQueryOrder(String field, String direction) {
			if (StringUtils.isBlank(field)) {
				return null;
			}
			if ("desc".equalsIgnoreCase(direction)) {
				return QueryOrder.DESC.setField(field);
			} else {
				return QueryOrder.ASC.setField(field);
			}
		}

		public String getField() {
			return field;
		}

		public QueryOrder setField(String field) {
			this.field = field;
			return this;
		}

		public String getDirection() {
			return direction;
		}

		public void setDirection(String direction) {
			this.direction = direction;
		}

	}

	void add(String id, String uuid, String appId, String name, long size, String ext, int mode) throws DBException;

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
	 * List my documents
	 * 
	 * @param uid
	 * @param offset
	 * @param limit
	 * @param labelId
	 * @param searchString
	 * @param queryOrder
	 * @return
	 * @throws DBException
	 */
	List<DocPo> listMyDocs(String uid, int offset, int limit, String labelId, String searchString, QueryOrder queryOrder) throws DBException;

	/**
	 * Get document count.
	 * 
	 * @param includeDeleted
	 * @return
	 * @throws DBException
	 */
	public long count(boolean includeDeleted) throws DBException;
}
