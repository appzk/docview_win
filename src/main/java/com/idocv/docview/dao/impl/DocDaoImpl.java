package com.idocv.docview.dao.impl;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.DocPo;
import com.idocv.docview.util.PinyinUtil;
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
	public void add(String app, String uid, String rid, String uuid,
			String name, long size, String ext, int status, String labelId)
			throws DBException {
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		if (StringUtils.isBlank(app)) {
			throw new DBException("应用为空！");
		}
		if (StringUtils.isBlank(rid)) {
			throw new DBException("文档id为空！");
		}
		if (StringUtils.isBlank(uuid)) {
			throw new DBException("文档uuid为空！");
		}
		if (StringUtils.isBlank(name)) {
			throw new DBException("文档名称为空！");
		}
		if (size <= 0) {
			throw new DBException("文档大小为0！");
		}
		if (StringUtils.isBlank(ext)) {
			throw new DBException("文档没有扩展名！");
		}
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.append(_ID, rid).append(UUID, uuid).append(APP, app)
				.append(NAME, name).append(SIZE, size).append(EXT, ext)
				.append(CTIME, time).append(UTIME, time).append(STATUS, status);
		if (StringUtils.isNotBlank(labelId)) {
			builder.append(LABELS, new String[] { labelId });
		}
		String pinyin = PinyinUtil.getSortPinYin(name);
		builder.append(PINYIN, pinyin);
		if (StringUtils.isNotBlank(uid)) {
			builder.append(UID, uid);
		}
		try {
			DBCollection coll = db.getCollection(COLL_DOC);
			coll.save(builder.get());
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}

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
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		DBObject query = QueryBuilder.start(UUID).is(uuid).get();
		BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$set")
				.append(UTIME, time).append(STATUS, status);
		try {
			DBCollection coll = db.getCollection(COLL_DOC);
			coll.update(query, ob.get(), false, true);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public boolean updateUrl(String uuid, String url) throws DBException {
		if (StringUtils.isEmpty(uuid)) {
			throw new DBException("请提供必要参数：uuid=" + uuid + ", url=" + url);
		}
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		DBObject query = QueryBuilder.start(UUID).is(uuid).get();
		BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$set").append(URL, url).append(UTIME, time);
		try {
			DBCollection coll = db.getCollection(COLL_DOC);
			coll.update(query, ob.get(), false, true);
			return true;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
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
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		DBObject query = QueryBuilder.start(UUID).is(uuid).get();
		BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$set").append(STATUS, mode).append(UTIME, time);
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
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
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
	public DocPo getUrl(String url, boolean includeDeleted) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start(URL).is(url);
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
	public List<DocPo> listMyDocs(String uid, int offset, int limit, String labelId, String searchString, QueryOrder queryOrder, int status) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start();
			if (-1 == status) {
				// 包括已删除

			} else if (0 == status) {
				// 包括私有文档
				query.and(STATUS).notEquals(-1);
			} else if (1 == status) {
				// 只显示公开文档
				query.and(STATUS).greaterThan(0);
			}

			if (StringUtils.isNotBlank(uid)) {
				query.and(UID).is(uid);
			}
			if (StringUtils.isNotBlank(labelId) && !"all".equalsIgnoreCase(labelId)) {
				query.and(LABELS).is(labelId);
			}

			if (StringUtils.isNotBlank(searchString)) {
				List<DBObject> searchQuery = new ArrayList<DBObject>();
				searchQuery.add(BasicDBObjectBuilder.start(NAME, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				searchQuery.add(BasicDBObjectBuilder.start(CTIME, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				searchQuery.add(BasicDBObjectBuilder.start(UUID, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				query.or(searchQuery.toArray(new DBObject[0]));
			}

			DBObject orderBy = BasicDBObjectBuilder.start().add(CTIME, -1).get();
			if (null != queryOrder) {
				if ("desc".equalsIgnoreCase(queryOrder.getDirection())) {
					orderBy = BasicDBObjectBuilder.start().add(queryOrder.getField(), -1).get();
				} else {
					orderBy = BasicDBObjectBuilder.start().add(queryOrder.getField(), 1).get();
				}
			}

			DBCollection coll = db.getCollection(COLL_DOC);
			DBCursor cur = coll.find(query.get()).sort(orderBy).skip(offset).limit(limit);
			return convertCur2Po(cur);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public int countMyDocs(String uid, String labelId, String searchString, int status) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start();
			if (-1 == status) {
				// 包括已删除

			} else if (0 == status) {
				// 包括私有文档
				query.and(STATUS).notEquals(-1);
			} else if (1 == status) {
				// 只显示公开文档
				query.and(STATUS).greaterThan(0);
			}

			if (StringUtils.isNotBlank(uid)) {
				query.and(UID).is(uid);
			}
			if (StringUtils.isNotBlank(labelId) && !"all".equalsIgnoreCase(labelId)) {
				query.and(LABELS).is(labelId);
			}

			if (StringUtils.isNotBlank(searchString)) {
				List<DBObject> searchQuery = new ArrayList<DBObject>();
				searchQuery.add(BasicDBObjectBuilder.start(NAME, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				searchQuery.add(BasicDBObjectBuilder.start(CTIME, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				searchQuery.add(BasicDBObjectBuilder.start(UUID, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				query.or(searchQuery.toArray(new DBObject[0]));
			}

			DBCollection coll = db.getCollection(COLL_DOC);
			int count = coll.find(query.get()).count();
			return count;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public List<DocPo> listAppDocs(String app, int offset, int limit, String labelId, String searchString, QueryOrder queryOrder, int status) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start();
			if (-1 == status) {
				// 包括已删除

			} else if (0 == status) {
				// 包括私有文档
				query.and(STATUS).notEquals(-1);
			} else if (1 == status) {
				// 只显示公开文档
				query.and(STATUS).greaterThan(0);
			}

			if (StringUtils.isBlank(app)) {
				throw new DBException("请提供应用名称！");
			}
			query.and(APP).is(app);
			if (StringUtils.isNotBlank(labelId) && !"all".equalsIgnoreCase(labelId)) {
				query.and(LABELS).is(labelId);
			}
			
			if (StringUtils.isNotBlank(searchString)) {
				List<DBObject> searchQuery = new ArrayList<DBObject>();
				searchQuery.add(BasicDBObjectBuilder.start(NAME, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				searchQuery.add(BasicDBObjectBuilder.start(CTIME, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				searchQuery.add(BasicDBObjectBuilder.start(UUID, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				query.or(searchQuery.toArray(new DBObject[0]));
			}
			
			DBObject orderBy = BasicDBObjectBuilder.start().add(CTIME, -1).get();
			if (null != queryOrder) {
				if ("desc".equalsIgnoreCase(queryOrder.getDirection())) {
					orderBy = BasicDBObjectBuilder.start().add(queryOrder.getField(), -1).get();
				} else {
					orderBy = BasicDBObjectBuilder.start().add(queryOrder.getField(), 1).get();
				}
			}
			
			DBCollection coll = db.getCollection(COLL_DOC);
			DBCursor cur = coll.find(query.get()).sort(orderBy).skip(offset).limit(limit);
			return convertCur2Po(cur);
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public int countAppDocs(String app, String labelId, String searchString, int status) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start();
			if (-1 == status) {
				// 包括已删除

			} else if (0 == status) {
				// 包括私有文档
				query.and(STATUS).notEquals(-1);
			} else if (1 == status) {
				// 只显示公开文档
				query.and(STATUS).greaterThan(0);
			}

			if (StringUtils.isBlank(app)) {
				throw new DBException("请提供应用名称！");
			}
			query.and(APP).is(app);
			if (StringUtils.isNotBlank(labelId) && !"all".equalsIgnoreCase(labelId)) {
				query.and(LABELS).is(labelId);
			}

			if (StringUtils.isNotBlank(searchString)) {
				List<DBObject> searchQuery = new ArrayList<DBObject>();
				searchQuery.add(BasicDBObjectBuilder.start(NAME, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				searchQuery.add(BasicDBObjectBuilder.start(CTIME, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				searchQuery.add(BasicDBObjectBuilder.start(UUID, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE)).get());
				query.or(searchQuery.toArray(new DBObject[0]));
			}

			DBCollection coll = db.getCollection(COLL_DOC);
			int count = coll.find(query.get()).count();
			return count;
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
		if (obj.containsField(APP)) {
			po.setApp(obj.get(APP).toString());
		}
		if (obj.containsField(UID)) {
			po.setUid(obj.get(UID).toString());
		}
		if (obj.containsField(_ID)) {
			po.setRid(obj.get(_ID).toString());
		}
		if (obj.containsField(UUID)) {
			po.setUuid(obj.get(UUID).toString());
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
			po.setCtime(obj.get(CTIME).toString());
		}
		if (obj.containsField(UTIME)) {
			po.setUtime(obj.get(UTIME).toString());
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
		return po;
	}
}
