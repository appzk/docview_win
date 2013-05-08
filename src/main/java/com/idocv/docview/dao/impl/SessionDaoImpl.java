package com.idocv.docview.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.SessionDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.SessionPo;
import com.idocv.docview.util.IdUtil;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;

@Repository
public class SessionDaoImpl extends BaseDaoImpl implements SessionDao, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		if (null != db) {
			DBCollection coll;
			coll = db.getCollection(COLL_SESSION);
			coll.ensureIndex(BasicDBObjectBuilder.start().add(_ID, 1).get());
		}
	}

	@Override
	public String add(String appId, String uuid) throws DBException {
		long time = System.currentTimeMillis();
		if (StringUtils.isBlank(appId) || StringUtils.isBlank(uuid)) {
			throw new DBException("Insufficient parameters!");
		}
		String objId = IdUtil.getObjectId();
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.append(_ID, objId).append(APP, appId).append(UUID, uuid)
				.append(CTIME, time);
		try {
			DBCollection coll = db.getCollection(COLL_SESSION);
			coll.save(builder.get());
			return objId;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public SessionPo get(String id) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start(_ID).is(id);
			DBCollection coll = db.getCollection(COLL_SESSION);
			DBObject obj = coll.findOne(query.get());
			return convertDBObject2Po(obj);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	private List<SessionPo> convertCur2Po(DBCursor cur) {
		List<SessionPo> list = new ArrayList<SessionPo>();
		if (null == cur) {
			return list;
		}
		DBObject obj;
		SessionPo po;
		while (cur.hasNext()) {
			obj = cur.next();
			po = convertDBObject2Po(obj);
			list.add(po);
		}
		return list;
	}

	private SessionPo convertDBObject2Po(DBObject obj) {
		if (null == obj) {
			return null;
		}
		SessionPo po = new SessionPo();
		if (obj.containsField(_ID)) {
			po.setId(obj.get(_ID).toString());
		}
		if (obj.containsField(APP)) {
			po.setAppId(obj.get(APP).toString());
		}
		if (obj.containsField(UUID)) {
			po.setUuid(obj.get(UUID).toString());
		}
		if (obj.containsField(CTIME)) {
			po.setCtime(obj.get(CTIME).toString());
		}
		return po;
	}
}