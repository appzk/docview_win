package com.idocv.docview.dao;

import java.util.List;

import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.LabelPo;

public interface LabelDao {

	/**
	 * Add label
	 * 
	 * @param uid
	 * @param name
	 * @param value
	 * @throws DBException
	 */
	LabelPo add(String uid, String name, String value) throws DBException;

	/**
	 * Delete label
	 */
	boolean delete(String id) throws DBException;

	/**
	 * Get label info
	 * 
	 * @param id
	 * @param includeDeleted whether include deleted labels
	 * @return
	 * @throws DBException
	 */
	LabelPo get(String id, boolean includeDeleted) throws DBException;

	/**
	 * get user label
	 * 
	 * @param uid
	 * @param name
	 * @return
	 * @throws DBException
	 */
	LabelPo get(String uid, String name) throws DBException;

	/**
	 * Get label list of a user
	 * 
	 * @param uid
	 * @return
	 * @throws DBException
	 */
	List<LabelPo> list(String uid) throws DBException;
}