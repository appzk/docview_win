package com.idocv.docview.dao.impl;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.DocPo;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;


@Repository
public class DocDaoImpl extends BaseDaoImpl implements DocDao, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO
	}

	@Override
	public void add(String id, String uuid, String appId, String name, long size, String ext, int mode) throws DBException {
		long time = System.currentTimeMillis();
		if (StringUtils.isBlank(id) || StringUtils.isBlank(uuid)
				|| StringUtils.isBlank(appId)
				|| StringUtils.isBlank(name) || size <= 0
				|| StringUtils.isBlank(ext)) {
			throw new DBException("Insufficient parameters!");
		}
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.append(_ID, id).append(UUID, uuid).append(APPID, appId)
				.append(NAME, name).append(SIZE, size).append(EXT, ext)
				.append(CTIME, time).append(STATUS, 0).append(MODE, mode);
		try {
			DBCollection coll = db.getCollection(COLL_DOC);
			coll.save(builder.get());
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}

	}

	@Override
	public void add(DocPo doc) throws DBException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean delete(String uuid) throws DBException {
		updateStatus(uuid, -1);
		return true;
	}

	private void updateStatus(String uuid, int status) throws DBException {
		if (StringUtils.isEmpty(uuid)) {
			throw new DBException("Insufficient parameters!");
		}

		DBObject query = QueryBuilder.start(UUID).is(uuid).get();
		BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$set")
				.append(UTIME, System.currentTimeMillis())
				.append(STATUS, status);
		try {
			DBCollection coll = db.getCollection(COLL_DOC);
			coll.update(query, ob.get(), false, true);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public boolean updateUrl(String uuid, String url) throws DBException {
		return false;
	}

	@Override
	public void logView(String uuid) throws DBException {
		log(uuid, VIEW);
	}

	@Override
	public void logDownload(String uuid) throws DBException {
		log(uuid, DOWNLOAD);
	}

	@Override
	public void updateMode(String uuid, int mode) throws DBException {
		if (StringUtils.isEmpty(uuid)) {
			throw new DBException("Insufficient parameters!");
		}
		long time = System.currentTimeMillis();
		DBObject query = QueryBuilder.start(UUID).is(uuid).get();
		BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$set").append(MODE, mode).append(UTIME, time);
		try {
			DBCollection coll = db.getCollection(COLL_DOC);
			coll.update(query, ob.get(), false, true);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	private void log(String uuid, String field) throws DBException {
		if (StringUtils.isEmpty(uuid)) {
			throw new DBException("Insufficient parameters!");
		}
		long time = System.currentTimeMillis();
		DBObject query = QueryBuilder.start(UUID).is(uuid).get();
		BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$push")
				.append(field, time).pop().push("$set").append(UTIME, time);
		try {
			DBCollection coll = db.getCollection(COLL_DOC);
			coll.update(query, ob.get(), false, true);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public DocPo get(String rid, boolean includeDeleted) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start(_ID).is(rid);
			if (!includeDeleted) {
				query.and(STATUS).notEquals(-1);
			}
			DBCollection coll = db.getCollection(COLL_DOC);
			DBObject obj = coll.findOne(query.get());
			return convertDBObject2Po(obj);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public DocPo getByUuid(String uuid, boolean includeDeleted) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start(UUID).is(uuid);
			if (!includeDeleted) {
				query.and(STATUS).notEquals(-1);
			}
			DBCollection coll = db.getCollection(COLL_DOC);
			DBObject obj = coll.findOne(query.get());
			return convertDBObject2Po(obj);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public DocPo getUrl(String url) throws DBException {
		return null;
	}

	@Override
	public List<DocPo> list(int offset, int limit) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start(STATUS).notEquals(-1);
			DBObject orderBy = BasicDBObjectBuilder.start().add(CTIME, -1).get();
			DBCollection coll = db.getCollection(COLL_DOC);
			DBCursor cur = coll.find(query.get()).sort(orderBy).skip(offset).limit(limit);
			return convertCur2Po(cur);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public long count(boolean includeDeleted) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start();
			if (!includeDeleted) {
				query.and(STATUS).notEquals(-1);
			}
			DBCollection coll = db.getCollection(COLL_DOC);
			return coll.count(query.get());
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	private List<DocPo> convertCur2Po(DBCursor cur) {
		List<DocPo> list = new ArrayList<DocPo>();
		if (null == cur) {
			return list;
		}
		DBObject obj;
		DocPo po;
		while (cur.hasNext()) {
			obj = cur.next();
			po = convertDBObject2Po(obj);
			list.add(po);
		}
		return list;
	}

	private DocPo convertDBObject2Po(DBObject obj) {
		if (null == obj) {
			return null;
		}
		DocPo po = new DocPo();
		if (obj.containsField(_ID)) {
			po.setRid(obj.get(_ID).toString());
		}
		if (obj.containsField(UUID)) {
			po.setUuid(obj.get(UUID).toString());
		}
		if (obj.containsField(APPID)) {
			po.setAppId(obj.get(APPID).toString());
		}
		if (obj.containsField(NAME)) {
			po.setName(obj.get(NAME).toString());
		}
		if (obj.containsField(SIZE)) {
			po.setSize(Long.valueOf(obj.get(SIZE).toString()));
		}
		if (obj.containsField(STATUS)) {
			po.setStatus(Integer.valueOf(obj.get(STATUS).toString()));
		}
		if (obj.containsField(CTIME)) {
			po.setCtime(Long.valueOf(obj.get(CTIME).toString()));
		}
		if (obj.containsField(UTIME)) {
			po.setUtime(Long.valueOf(obj.get(UTIME).toString()));
		}
		if (obj.containsField(EXT)) {
			po.setExt(obj.get(EXT).toString());
		}
		if (obj.containsField(URL)) {
			po.setUrl(obj.get(URL).toString());
		}
		if (obj.containsField(VIEW)) {
			po.setViewLog((List<Long>) obj.get(VIEW));
		}
		if (obj.containsField(DOWNLOAD)) {
			po.setDownloadLog((List<Long>) obj.get(DOWNLOAD));
		}
		if (obj.containsField(MODE)) {
			po.setMode(Integer.valueOf(obj.get(MODE).toString()));
		}
		return po;
	}
}
