package com.idocv.docview.dao.impl;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.CacheDao;
import com.idocv.docview.exception.DBException;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;


@Repository
public class CacheDaoImpl extends BaseDaoImpl implements CacheDao, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(CacheDaoImpl.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (null != db) {
			DBCollection coll;
			coll = db.getCollection(COLL_CACHE);
			coll.ensureIndex(BasicDBObjectBuilder.start().add(_ID, 1).get());
		}
	}

	@Override
	public void setGlobal(String key, String value) throws DBException {
		if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
			logger.error("设置全局变量失败：KEY(" + key + ")和VALUE(" + value + ")不能为空！");
			throw new DBException("设置全局变量失败：KEY(" + key + ")和VALUE(" + value
					+ ")不能为空！");
		}
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		DBObject query = QueryBuilder.start(_ID).is("global").get();
		BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$set").append(UTIME, time).append(key, value);
		try {
			DBCollection coll = db.getCollection(COLL_CACHE);
			coll.update(query, ob.get(), true, true);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public String getGlobal(String key) throws DBException {
		if (StringUtils.isBlank(key)) {
			logger.error("获取全局变量失败：KEY(" + key + ")不能为空！");
			throw new DBException("获取全局变量失败：KEY(" + key + ")不能为空！");
		}
		try {
			DBObject query = QueryBuilder.start(_ID).is("global").get();
			DBCollection coll = db.getCollection(COLL_CACHE);
			DBObject obj = coll.findOne(query);
			if (null != obj && obj.containsField(key) && null != obj.get(key)) {
				return obj.get(key).toString();
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}
}