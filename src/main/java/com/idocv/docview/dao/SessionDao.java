package com.idocv.docview.dao;

import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.SessionPo;

public interface SessionDao extends BaseDao {

	String add(String appId, String uuid) throws DBException;

	SessionPo get(String id) throws DBException;
}