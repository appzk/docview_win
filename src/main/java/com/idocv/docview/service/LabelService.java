package com.idocv.docview.service;

import java.util.List;

import com.idocv.docview.exception.DBException;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.vo.LabelVo;

/**
 * Document list label service
 * 
 * @author Godwin
 * @version 2013-04-14 1.0
 * 
 */
public interface LabelService {

	/**
	 * Initialize user labels
	 * 
	 * @param uid
	 * @throws DocServiceException
	 */
	void initUser(String uid) throws DocServiceException;

	/**
	 * Add label
	 * 
	 * @param uid
	 * @param name
	 * @param value
	 * @throws DBException
	 */
	LabelVo add(String uid, String name, String value) throws DocServiceException;

	/**
	 * Delete label
	 */
	boolean delete(String id) throws DocServiceException;

	/**
	 * Get label info
	 * 
	 * @param id
	 * @param includeDeleted whether include deleted labels
	 * @return
	 * @throws DBException
	 */
	LabelVo get(String id, boolean includeDeleted) throws DocServiceException;

	/**
	 * Get label list of a user
	 * 
	 * @param uid
	 * @return
	 * @throws DBException
	 */
	List<LabelVo> list(String uid) throws DocServiceException;
}