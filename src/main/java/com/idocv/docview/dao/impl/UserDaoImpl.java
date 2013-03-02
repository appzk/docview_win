package com.idocv.docview.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.UserDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.UserPo;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;

@Repository
public class UserDaoImpl extends BaseDaoImpl implements UserDao, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO

	}

	@Override
	public UserPo signUp(String appId, String username, String password, String email) throws DBException {
		long time = System.currentTimeMillis();
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(username)
				|| StringUtils.isBlank(password) || StringUtils.isBlank(email)) {
			throw new DBException("Insufficient parameters!");
		}
		String objId = new ObjectId().toString();
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.append(_ID, objId).append(APPID, appId)
				.append(USERNAME, username).append(PASSWORD, password)
				.append(EMAIL, email).append(CTIME, time);
		try {
			DBCollection coll = db.getCollection(COLL_USER);
			coll.save(builder.get());
			UserPo po = new UserPo();
			po.setId(objId);
			po.setApp(appId);
			po.setUsername(username);
			po.setPassword(password);
			po.setEmail(email);
			return po;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public boolean isExistUsername(String username) throws DBException {
		if (StringUtils.isBlank(username)) {
			throw new DBException("Insufficient parameters!");
		}
		try {
			QueryBuilder query = QueryBuilder.start(USERNAME).is(username);
			DBCollection coll = db.getCollection(COLL_USER);
			return coll.count(query.get()) > 0 ? true : false;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public boolean isExistEmail(String email) throws DBException {
		if (StringUtils.isBlank(email)) {
			throw new DBException("Insufficient parameters!");
		}
		try {
			QueryBuilder query = QueryBuilder.start(EMAIL).is(email);
			DBCollection coll = db.getCollection(COLL_USER);
			return coll.count(query.get()) > 0 ? true : false;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}
}
