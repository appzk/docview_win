package com.idocv.docview.dao.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.BaseDao;
import com.mongodb.DB;

@Repository
public class BaseDaoImpl implements BaseDao {
	
	public static final String _ID = "_id";
	public static final String APPID = "appid";
	public static final String NAME = "name";
	public static final String SIZE = "size";
	public static final String EXT = "ext";
	public static final String UID = "uid";
	public static final String CTIME = "ctime";
	public static final String UTIME = "utime";
	public static final String DTIME = "dtime";
	public static final String STATUS = "status";
	public static final String ADDRESS = "address";
	public static final String KEY = "key";
	public static final String PHONE = "phone";

	@Resource
	protected DB db;

}
