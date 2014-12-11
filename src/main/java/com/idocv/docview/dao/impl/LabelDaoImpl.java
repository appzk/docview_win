package com.idocv.docview.dao.impl;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.LabelDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.LabelPo;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;


@Repository
public class LabelDaoImpl extends BaseDaoImpl implements LabelDao, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(LabelDaoImpl.class);
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO
	}

	@Override
	public LabelPo add(String uid, String name, String value) throws DBException {
		String id = new ObjectId().toString();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		if (StringUtils.isBlank(uid) || StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
			logger.error("Insufficient parameters: uid=" + uid + ", name=" + name + ", value=" + value);
			throw new DBException("Insufficient parameters!");
		}
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.append(_ID, id).append(UID, uid).append(NAME, name)
				.append(VALUE, value).append(CTIME, time).append(UTIME, time)
				.append(STATUS, 0);
		try {
			DBCollection coll = db.getCollection(COLL_LABEL);
			coll.save(builder.get());
			LabelPo po = new LabelPo();
			po.setUid(uid);
			po.setName(name);
			po.setValue(value);
			po.setCtime(time);
			po.setId(id);
			return po;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public boolean delete(String id) throws DBException {
		updateStatus(id, -1);
		return true;
	}

	private void updateStatus(String id, int status) throws DBException {
		if (StringUtils.isEmpty(id)) {
			throw new DBException("Insufficient parameters!");
		}
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		DBObject query = QueryBuilder.start(_ID).is(id).get();
		BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$set")
				.append(UTIME, time).append(STATUS, status);
		try {
			DBCollection coll = db.getCollection(COLL_LABEL);
			coll.update(query, ob.get(), false, true);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public LabelPo get(String id, boolean includeDeleted) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start(_ID).is(id);
			if (!includeDeleted) {
				query.and(STATUS).notEquals(-1);
			}
			DBCollection coll = db.getCollection(COLL_LABEL);
			DBObject obj = coll.findOne(query.get());
			return convertDBObject2Po(obj);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public LabelPo get(String uid, String labelName) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start(STATUS).notEquals(-1);
			if (StringUtils.isNotBlank(uid)) {
				query.and(UID).is(uid);
			}
			if (StringUtils.isNotBlank(NAME)) {
				query.and(NAME).is(labelName);
			}
			DBCollection coll = db.getCollection(COLL_LABEL);
			DBObject obj = coll.findOne(query.get());
			return convertDBObject2Po(obj);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public List<LabelPo> list(String uid) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start(STATUS).notEquals(-1);
			DBObject orderBy = BasicDBObjectBuilder.start().add(CTIME, 1).get();
			DBCollection coll = db.getCollection(COLL_LABEL);
			DBCursor cur = coll.find(query.get()).sort(orderBy);
			return convertCur2Po(cur);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	private List<LabelPo> convertCur2Po(DBCursor cur) {
		List<LabelPo> list = new ArrayList<LabelPo>();
		if (null == cur) {
			return list;
		}
		DBObject obj;
		LabelPo po;
		while (cur.hasNext()) {
			obj = cur.next();
			po = convertDBObject2Po(obj);
			list.add(po);
		}
		return list;
	}

	private LabelPo convertDBObject2Po(DBObject obj) {
		if (null == obj) {
			return null;
		}
		LabelPo po = new LabelPo();
		if (obj.containsField(_ID)) {
			po.setId(obj.get(_ID).toString());
		}
		if (obj.containsField(UID)) {
			po.setUid(obj.get(UID).toString());
		}
		if (obj.containsField(NAME)) {
			po.setName(obj.get(NAME).toString());
		}
		if (obj.containsField(VALUE)) {
			po.setValue(obj.get(VALUE).toString());
		}
		if (obj.containsField(STATUS)) {
			po.setStatus(Integer.valueOf(obj.get(STATUS).toString()));
		}
		if (obj.containsField(CTIME)) {
			po.setCtime(obj.get(CTIME).toString());
		}
		if (obj.containsField(UTIME)) {
			po.setUtime(obj.get(UTIME).toString());
		}
		return po;
	}
}
