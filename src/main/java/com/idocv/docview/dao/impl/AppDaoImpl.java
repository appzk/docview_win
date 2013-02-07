package com.idocv.docview.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.AppDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.AppPo;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;

@Repository
public class AppDaoImpl extends BaseDaoImpl implements AppDao, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		if (null != db) {
			DBCollection coll;
			coll = db.getCollection(COLL_APP);
			coll.ensureIndex(BasicDBObjectBuilder.start().add(_ID, 1).get());

		}

	}

	@Override
	public boolean add(String id, String name, String key, String phone) throws DBException {
		long time = System.currentTimeMillis();
		if (StringUtils.isBlank(id) || StringUtils.isBlank(name) || StringUtils.isBlank(key) || StringUtils.isBlank(phone)) {
			throw new DBException("Insufficient parameters!");
		}
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.append(_ID, id).append(NAME, name).append(KEY, key)
				.append(PHONE, phone).append(STATUS, 0).append(CTIME, time);
		try {
			DBCollection coll = db.getCollection(COLL_APP);
			coll.save(builder.get());
			return true;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public AppPo add(AppPo appPo) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String appId) throws DBException {
		updateStatus(appId, -1);
	}

	private void updateStatus(String appId, int status) throws DBException {
		if (StringUtils.isEmpty(appId)) {
			throw new DBException("Insufficient parameters!");
		}

		DBObject query = QueryBuilder.start(_ID).is(appId).get();
		BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$set")
				.append(UTIME, System.currentTimeMillis())
				.append(STATUS, status);
		try {
			DBCollection coll = db.getCollection(COLL_APP);
			coll.update(query, ob.get(), false, true);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}
}
