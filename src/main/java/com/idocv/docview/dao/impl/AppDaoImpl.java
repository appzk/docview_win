package com.idocv.docview.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.AppDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.AppPo;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
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
	public boolean add(String id, String name, String token, String phone) throws DBException {
		long time = System.currentTimeMillis();
		if (StringUtils.isBlank(id) || StringUtils.isBlank(name) || StringUtils.isBlank(token) || StringUtils.isBlank(phone)) {
			throw new DBException("Insufficient parameters!");
		}
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.append(_ID, id).append(NAME, name).append(TOKEN, token)
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

	@Override
	public AppPo get(String id) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start(_ID).is(id);
			DBCollection coll = db.getCollection(COLL_APP);
			DBObject obj = coll.findOne(query.get());
			return convertDBObject2Po(obj);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public AppPo getByToken(String token) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start(TOKEN).is(token);
			DBCollection coll = db.getCollection(COLL_APP);
			DBObject obj = coll.findOne(query.get());
			return convertDBObject2Po(obj);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public List<AppPo> list(int offset, int limit) throws DBException {
		try {
			DBObject orderBy = BasicDBObjectBuilder.start().add(CTIME, -1).get();
			DBCollection coll = db.getCollection(COLL_APP);
			DBCursor cur = coll.find().sort(orderBy).skip(offset).limit(limit);
			return convertCur2Po(cur);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	private List<AppPo> convertCur2Po(DBCursor cur) {
		List<AppPo> list = new ArrayList<AppPo>();
		if (null == cur) {
			return list;
		}
		DBObject obj;
		AppPo po;
		while (cur.hasNext()) {
			obj = cur.next();
			po = convertDBObject2Po(obj);
			list.add(po);
		}
		return list;
	}

	private AppPo convertDBObject2Po(DBObject obj) {
		if (null == obj) {
			return null;
		}
		AppPo po = new AppPo();
		if (obj.containsField(_ID)) {
			po.setId(obj.get(_ID).toString());
		}
		if (obj.containsField(NAME)) {
			po.setName(obj.get(NAME).toString());
		}
		if (obj.containsField(TOKEN)) {
			po.setKey(obj.get(TOKEN).toString());
		}
		if (obj.containsField(IPS)) {
			po.setIps((Collection<String>) obj.get(IPS));
		}
		if (obj.containsField(PHONE)) {
			po.setPhone(obj.get(PHONE).toString());
		}
		if (obj.containsField(CTIME)) {
			po.setCtime(Long.valueOf(obj.get(CTIME).toString()));
		}
		if (obj.containsField(UTIME)) {
			po.setUtime(Long.valueOf(obj.get(UTIME).toString()));
		}
		if (obj.containsField(ADDRESS)) {
			po.setAddress(obj.get(ADDRESS).toString());
		}
		if (obj.containsField(UTIME)) {
			po.setUtime(Long.valueOf(obj.get(UTIME).toString()));
		}
		return po;
	}
}
