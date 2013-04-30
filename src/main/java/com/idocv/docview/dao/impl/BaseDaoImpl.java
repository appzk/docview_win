package com.idocv.docview.dao.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.BaseDao;
import com.mongodb.DB;

@Repository
public class BaseDaoImpl implements BaseDao {
	
	public static final String _ID = "_id";
	public static final String RID = "rid";
	public static final String UUID = "uuid";
	public static final String APP = "app";
	public static final String NAME = "name";
	public static final String VALUE = "value";
	public static final String PINYIN = "pinyin";
	public static final String SIZE = "size";
	public static final String EXT = "ext";
	public static final String URL = "url";
	public static final String UID = "uid";
	public static final String SIDS = "sids";
	public static final String CTIME = "ctime";
	public static final String UTIME = "utime";
	public static final String DTIME = "dtime";
	public static final String STATUS = "status";

	public static final String LABELS = "labels";

	public static final String VIEW = "view";
	public static final String DOWNLOAD = "download";

	public static final String ADDRESS = "address";
	public static final String TOKEN = "token";
	public static final String IPS = "ips";
	public static final String PHONE = "phone";

	// User info
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String EMAIL = "email";

	@Resource
	protected DB db;

}
