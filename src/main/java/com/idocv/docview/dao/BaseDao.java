package com.idocv.docview.dao;

public interface BaseDao {
	// collections
	static final String COLL_APP = "app";
	static final String COLL_USER = "user";
	static final String COLL_DOC = "doc";
	static final String COLL_LABEL = "label";
	static final String COLL_SESSION = "session";
	static final String COLL_CACHE = "cache";

	// fields
	static final String _ID = "_id";
	static final String RID = "rid";
	static final String UUID = "uuid";
	static final String MD5 = "md5";
	static final String APP = "app";
	static final String NAME = "name";
	static final String VALUE = "value";
	static final String PINYIN = "pinyin";
	static final String SIZE = "size";
	static final String EXT = "ext";
	static final String URL = "url";
	static final String UID = "uid";
	static final String SIDS = "sids";
	static final String CTIME = "ctime";
	static final String UTIME = "utime";
	static final String DTIME = "dtime";
	static final String STATUS = "status";
	static final String STATUS_CONVERT = "convert";
	static final int STATUS_CONVERT_INIT = 0;
	static final int STATUS_CONVERT_SUCCESS = 1;
	static final int STATUS_CONVERT_FAIL = -1;
	static final int STATUS_CONVERT_NOT_SUPPORT = -2;

	static final String LABELS = "labels";

	static final String VIEW = "view";
	static final String DOWNLOAD = "download";

	static final String METAS = "metas";

	static final String ADDRESS = "address";
	static final String TOKEN = "token";
	static final String IPS = "ips";
	static final String PHONE = "phone";

	// User info
	static final String USERNAME = "username";
	static final String PASSWORD = "password";
	static final String EMAIL = "email";
}