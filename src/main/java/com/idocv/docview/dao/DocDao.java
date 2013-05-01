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

	/**
	 * 添加文档
	 * 
	 * @param app 应用id
	 * @param uid 用户id
	 * @param rid 文档id（用于主键，文档存储路径）
	 * @param uuid 文档唯一标示id
	 * @param name 名称
	 * @param size 大小
	 * @param ext 扩展名
	 * @param status 文档状态，-1：已删除；0：私有文档；1：公开文档
	 * @param labelId 文档所属标签id
	 * @throws DBException
	 */
	void add(String app, String uid, String rid, String uuid, String name, long size, String ext, int status, String labelId) throws DBException;

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
	 * @param status -1：包括已删除文档；0：包括私有文档；1：只列出公开文档
	 * @return
	 * @throws DBException
	 */
	List<DocPo> listMyDocs(String uid, int offset, int limit, String labelId, String searchString, QueryOrder queryOrder, int status) throws DBException;
	
	/**
	 * Count my documents
	 * 
	 * @param uid
	 * @param labelId
	 * @param searchString
	 * @param status -1：包括已删除文档；0：包括私有文档；1：只列出公开文档
	 * @return
	 * @throws DBException
	 */
	int countMyDocs(String uid, String labelId, String searchString, int status) throws DBException;
	
	/**
	 * List Application documents
	 * 
	 * @param app
	 * @param offset
	 * @param limit
	 * @param labelId
	 * @param searchString
	 * @param queryOrder
	 * @param status -1：包括已删除文档；0：包括私有文档；1：只列出公开文档
	 * @return
	 * @throws DBException
	 */
	List<DocPo> listAppDocs(String app, int offset, int limit, String labelId, String searchString, QueryOrder queryOrder, int status) throws DBException;
	
	/**
	 * Count app documents
	 * 
	 * @param app
	 * @param labelId
	 * @param searchString
	 * @param status -1：包括已删除文档；0：包括私有文档；1：只列出公开文档
	 * @return
	 * @throws DBException
	 */
	int countAppDocs(String app, String labelId, String searchString, int status) throws DBException;

	/**
	 * Get document count.
	 * 
	 * @param includeDeleted
	 * @return
	 * @throws DBException
	 */
	public long count(boolean includeDeleted) throws DBException;
}
