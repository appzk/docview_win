package com.idocv.docview.dao;

import java.util.List;

import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.AppPo;

public interface AppDao extends BaseDao {

	boolean add(String id, String name, String key, String phone) throws DBException;
	
	AppPo add(AppPo appPo) throws DBException;

	void delete(String appId) throws DBException;

	AppPo get(String id) throws DBException;

	AppPo getByToken(String key) throws DBException;

	List<AppPo> list(int offset, int limit) throws DBException;
}