package com.idocv.docview.dao.impl;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.DocPo;
import com.idocv.docview.util.PinyinUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteConcern;


@Repository
public class DocDaoImpl extends BaseDaoImpl implements DocDao, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		if (null != db) {
			DBCollection coll;
			coll = db.getCollection(COLL_DOC);
			coll.createIndex(BasicDBObjectBuilder.start().add(_ID, 1).get());
			coll.createIndex(BasicDBObjectBuilder.start().add(UUID, 1).get());
			coll.createIndex(BasicDBObjectBuilder.start().add(CTIME, 1).get());
			coll.createIndex(BasicDBObjectBuilder.start().add(STATUS, 1).get());
			coll.createIndex(BasicDBObjectBuilder.start().add(STATUS_CONVERT, 1).get());
		}
	}

	@Override
	public void add(String app, String uid, String rid, String uuid,
			String md5, String name, int size, String ext, int status,
			String labelId, Map<String, Object> metas, String url)
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
		if (StringUtils.isBlank(md5)) {
			throw new DBException("文档md5为空！");
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
				.append(_ID, rid).append(UUID, uuid).append(MD5, md5)
				.append(APP, app).append(NAME, name).append(SIZE, size)
				.append(EXT, ext).append(CTIME, time).append(UTIME, time)
				.append(STATUS, status);
		if (StringUtils.isNotBlank(labelId)) {
			builder.append(LABELS, new String[] { labelId });
		}
		if (!CollectionUtils.isEmpty(metas)) {
			builder.append(METAS, metas);
		}
		if (StringUtils.isNotBlank(url)) {
			builder.append(URL, url);
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
	public boolean delete(String uuid, boolean isDeleteRecord) throws DBException {
		if (isDeleteRecord) {
			if (StringUtils.isEmpty(uuid)) {
				throw new DBException("UUID is empty!");
			}
			DBObject query = QueryBuilder.start(UUID).is(uuid).get();
			try {
				DBCollection coll = db.getCollection(COLL_DOC);
				coll.remove(query, WriteConcern.UNACKNOWLEDGED);
			} catch (MongoException e) {
				throw new DBException(e.getMessage());
			}
		} else {
			updateStatus(uuid, -1);
		}
		return true;
	}

	@Override
	public boolean deleteByTimeRange(Date startTime, Date endTime, boolean isDeleteRecord) throws DBException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTimeStr = df.format(startTime);
		String endTimeStr = df.format(endTime);
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		DBObject query = QueryBuilder.start(CTIME).greaterThanEquals(startTimeStr).and(CTIME).lessThanEquals(endTimeStr).get();
		try {
			DBCollection coll = db.getCollection(COLL_DOC);
			if (isDeleteRecord) {
				coll.remove(query, WriteConcern.UNACKNOWLEDGED);
			} else {
				BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$set").append(UTIME, time).append(STATUS, -1);
				coll.update(query, ob.get(), false, true);
			}
			return true;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}
	
	@Override
	public boolean updateFieldById(String id, String name, Object value) throws DBException {
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(name)) {
			throw new DBException("请提供必要参数：id=" + id + ", name=" + name);
		}
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		DBObject query = QueryBuilder.start(_ID).is(id).get();
		BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$set").append(UTIME, time);
		if (null != value) {
			ob.append(name, value);
		} else {
			ob.pop().push("$unset").append(name, 1);
		}
		try {
			DBCollection coll = db.getCollection(COLL_DOC);
			coll.update(query, ob.get(), false, true);
			return true;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}
	
	@Override
	public boolean updateFieldByUuid(String uuid, String name, Object value) throws DBException {
		if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(name)) {
			throw new DBException("请提供必要参数：uuid=" + uuid + ", name=" + name);
		}
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		DBObject query = QueryBuilder.start(UUID).is(uuid).get();
		BasicDBObjectBuilder ob = BasicDBObjectBuilder.start().push("$set").append(UTIME, time);
		if (null != value) {
			ob.append(name, value);
		} else {
			ob.pop().push("$unset").append(name, 1);
		}
		try {
			DBCollection coll = db.getCollection(COLL_DOC);
			coll.update(query, ob.get(), false, true);
			return true;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
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
	public DocPo getByMd5(String md5, boolean includeDeleted) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start(MD5).is(md5);
			if (!includeDeleted) {
				query.and(STATUS).notEquals(-1);
			}
			DBObject fields = BasicDBObjectBuilder.start().get();
			DBObject order = BasicDBObjectBuilder.start(CTIME, -1).get();
			DBCollection coll = db.getCollection(COLL_DOC);
			DBObject obj = coll.findOne(query.get(), fields, order);
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
			DBObject fields = BasicDBObjectBuilder.start().get();
			DBObject order = BasicDBObjectBuilder.start(CTIME, -1).get();
			DBCollection coll = db.getCollection(COLL_DOC);
			DBObject obj = coll.findOne(query.get(), fields, order);
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
	public int countAppDocs(String app, String labelId, String searchString, int status, long startTime, long endTime) throws DBException {
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

			if (StringUtils.isNotBlank(app)) {
				query.and(APP).is(app);
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
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime > 0) {
				String startTimeString = df.format(new Date(startTime));
				query.and(CTIME).greaterThanEquals(startTimeString);
			}
			if (endTime > 0) {
				String endTimeString = df.format(new Date(endTime));
				query.and(CTIME).lessThanEquals(endTimeString);
			}

			DBCollection coll = db.getCollection(COLL_DOC);
			int count = coll.find(query.get()).count();
			return count;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}

	@Override
	public List<String> listDocIdsNotConverted(String startTime, int size) throws DBException {
		List<String> idList = new ArrayList<String>();
		try {
			List<DBObject> objs = new ArrayList<DBObject>();
			objs.add(BasicDBObjectBuilder.start(STATUS_CONVERT, STATUS_CONVERT_INIT).get());
			objs.add(BasicDBObjectBuilder.start().push(STATUS_CONVERT).add("$exists", false).get());
			QueryBuilder query = QueryBuilder.start().or(objs.toArray(new DBObject[] {}));
			if (StringUtils.isNotBlank(startTime) && startTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
				query.and(CTIME).greaterThan(startTime);
			}
			DBObject orderBy = BasicDBObjectBuilder.start().add(CTIME, 1).get();
			DBCollection coll = db.getCollection(COLL_DOC);
			DBCursor cur = coll.find(query.get(), new BasicDBObject(_ID, 1)).sort(orderBy).limit(size);
			while (cur.hasNext()) {
				DBObject obj = cur.next();
				if (obj.containsField(_ID) && null != obj.get(_ID)) {
					idList.add(obj.get(_ID).toString());
				}
			}
			return idList;
		} catch (MongoException e) {
			throw new DBException(e.getMessage());
		}
	}
	
	@Override
	public List<String> listDocIdsConvertError(String startTime, int size) throws DBException {
		List<String> idList = new ArrayList<String>();
		try {
			QueryBuilder query = QueryBuilder.start(STATUS_CONVERT).is(STATUS_CONVERT_FAIL).and(STATUS).notEquals(-1);
			if (StringUtils.isNotBlank(startTime) && startTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
				query.and(CTIME).greaterThan(startTime);
			}
			DBObject orderBy = BasicDBObjectBuilder.start().add(CTIME, 1).get();
			DBCollection coll = db.getCollection(COLL_DOC);
			DBCursor cur = coll.find(query.get(), new BasicDBObject(_ID, 1)).sort(orderBy).limit(size);
			while (cur.hasNext()) {
				DBObject obj = cur.next();
				if (obj.containsField(_ID) && null != obj.get(_ID)) {
					idList.add(obj.get(_ID).toString());
				}
			}
			return idList;
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

	@Override
	public List<DocPo> listNewlyAddedFiles(String startTime, int size) throws DBException {
		try {
			QueryBuilder query = QueryBuilder.start("metas.remote").notEquals("1");
			if (StringUtils.isNotBlank(startTime) && startTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
				query.and(CTIME).greaterThan(startTime);
			}
			DBObject orderBy = BasicDBObjectBuilder.start().add(CTIME, 1).get();
			DBCollection coll = db.getCollection(COLL_DOC);
			DBCursor cur = coll.find(query.get()).sort(orderBy).limit(size);
			return convertCur2Po(cur);
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

	@SuppressWarnings("unchecked")
	private DocPo convertDBObject2Po(DBObject obj) {
		if (null == obj) {
			return null;
		}
		DocPo po = new DocPo();
		if (obj.containsField(_ID) && null != obj.get(_ID)) {
			po.setRid(obj.get(_ID).toString());
		}
		if (obj.containsField(APP) && null != obj.get(APP)) {
			po.setApp(obj.get(APP).toString());
		}
		if (obj.containsField(UID) && null != obj.get(UID)) {
			po.setUid(obj.get(UID).toString());
		}
		if (obj.containsField(UUID) && null != obj.get(UUID)) {
			po.setUuid(obj.get(UUID).toString());
		}
		if (obj.containsField(MD5) && null != obj.get(MD5)) {
			po.setMd5(obj.get(MD5).toString());
		}
		if (obj.containsField(NAME) && null != obj.get(NAME)) {
			po.setName(obj.get(NAME).toString());
		}
		if (obj.containsField(SIZE) && null != obj.get(SIZE)) {
			po.setSize(Long.valueOf(obj.get(SIZE).toString()));
		}
		if (obj.containsField(STATUS) && null != obj.get(STATUS)) {
			po.setStatus(Integer.valueOf(obj.get(STATUS).toString()));
		}
		if (obj.containsField(STATUS_CONVERT) && null != obj.get(STATUS_CONVERT)) {
			po.setConvert(Integer.valueOf(obj.get(STATUS_CONVERT).toString()));
		}
		if (obj.containsField(CTIME) && null != obj.get(CTIME)) {
			po.setCtime(obj.get(CTIME).toString());
		}
		if (obj.containsField(UTIME) && null != obj.get(UTIME)) {
			po.setUtime(obj.get(UTIME).toString());
		}
		if (obj.containsField(EXT) && null != obj.get(EXT)) {
			po.setExt(obj.get(EXT).toString());
		}
		if (obj.containsField(URL) && null != obj.get(URL)) {
			po.setUrl(obj.get(URL).toString());
		}
		if (obj.containsField(VIEW) && null != obj.get(VIEW)) {
			po.setViewLog((List<Long>) obj.get(VIEW));
		}
		if (obj.containsField(DOWNLOAD) && null != obj.get(DOWNLOAD)) {
			po.setDownloadLog((List<Long>) obj.get(DOWNLOAD));
		}
		if (obj.containsField(METAS) && null != obj.get(METAS)) {
			po.setMetas((Map<String, Object>) obj.get(METAS));
		}
		return po;
	}
}
