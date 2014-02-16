package com.idocv.docview.dao;

import com.idocv.docview.exception.DBException;

public interface CacheDao {

	void setGlobal(String key, String value) throws DBException;
	
	String getGlobal(String key) throws DBException;
}