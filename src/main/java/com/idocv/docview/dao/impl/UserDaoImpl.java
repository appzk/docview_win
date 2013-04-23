package com.idocv.docview.dao.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.UserDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.UserPo;
import com.idocv.docview.util.UidUtil;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
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
		String sid = UidUtil.getSid(objId, "appId");
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.append(_ID, objId).append(APP, appId)
				.append(USERNAME, username).append(PASSWORD, password)
				.append(EMAIL, email).append(CTIME, time)
				.append(SIDS, Arrays.asList(new String[] { sid }));
		try {
			DBCollection coll = db.getCollection(COLL_USER);
			coll.save(builder.get());
			UserPo po = new UserPo();
			po.setId(objId);
			po.setAppId(appId);
			po.setUsername(username);
			po.setPassword(password);
			po.setEmail(email);
			po.setSids(Arrays.asList(new String[] { sid }));
			return po;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public String addSid(String uid) throws DBException {
		if (StringUtils.isBlank(uid)) {
			throw new DBException("Insufficient parameters!");
		}
		String sid = UidUtil.getSid(uid, "wev");
		QueryBuilder query = QueryBuilder.start(_ID).is(uid);
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start().push("$push").add(SIDS, sid);
		try {
			DBCollection coll = db.getCollection(COLL_USER);
			coll.update(query.get(), builder.get(), false, false);
			return sid;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public void logout(String sid) throws DBException {
		if (StringUtils.isBlank(sid)) {
			throw new DBException("Insufficient parameters!");
		}
		String uid = UidUtil.getUid(sid);
		QueryBuilder query = QueryBuilder.start(_ID).is(uid);
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start().push("$pull").add(SIDS, sid);
		try {
			DBCollection coll = db.getCollection(COLL_USER);
			coll.update(query.get(), builder.get(), false, false);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public UserPo getByUsername(String username) throws DBException {
		if (StringUtils.isBlank(username)) {
			throw new DBException("Insufficient parameters!");
		}
		try {
			QueryBuilder query = QueryBuilder.start(USERNAME).regex(Pattern.compile(username, Pattern.CASE_INSENSITIVE));
			DBCollection coll = db.getCollection(COLL_USER);
			return convertDBObject2Po(coll.findOne(query.get()));
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public UserPo getByEmail(String email) throws DBException {
		if (StringUtils.isBlank(email)) {
			throw new DBException("Insufficient parameters!");
		}
		try {
			QueryBuilder query = QueryBuilder.start(EMAIL).regex(Pattern.compile(email, Pattern.CASE_INSENSITIVE));
			DBCollection coll = db.getCollection(COLL_USER);
			return convertDBObject2Po(coll.findOne(query.get()));
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public UserPo getBySid(String sid) throws DBException {
		if (StringUtils.isBlank(sid)) {
			throw new DBException("Insufficient parameters!");
		}
		try {
			QueryBuilder query = QueryBuilder.start(SIDS).is(sid);
			DBCollection coll = db.getCollection(COLL_USER);
			return convertDBObject2Po(coll.findOne(query.get()));
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	private UserPo convertDBObject2Po(DBObject obj) {
		if (null == obj) {
			return null;
		}
		UserPo po = new UserPo();
		if (obj.containsField(_ID)) {
			po.setId(obj.get(_ID).toString());
		}
		if (obj.containsField(APP)) {
			po.setAppId(obj.get(APP).toString());
		}
		if (obj.containsField(USERNAME)) {
			po.setUsername(obj.get(USERNAME).toString());
		}
		if (obj.containsField(PASSWORD)) {
			po.setPassword(obj.get(PASSWORD).toString());
		}
		if (obj.containsField(EMAIL)) {
			po.setEmail(obj.get(EMAIL).toString());
		}
		if (obj.containsField(CTIME)) {
			po.setCtime(Long.valueOf(obj.get(CTIME).toString()));
		}
		if (obj.containsField(SIDS)) {
			po.setSids((Collection<String>) obj.get(SIDS));
		}
		return po;
	}
}