package com.idocv.docview.dao.impl;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.common.Paging;
import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.DocPo;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
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
	public void add(String id, String appId, String name, long size, String ext) throws DBException {
		long time = System.currentTimeMillis();
		if (StringUtils.isBlank(id) || StringUtils.isBlank(appId)
				|| StringUtils.isBlank(name) || size <= 0
				|| StringUtils.isBlank(ext)) {
			throw new DBException("Insufficient parameters!");
		}
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
				.append(_ID, id).append(APPID, appId).append(NAME, name)
				.append(SIZE, size).append(EXT, ext).append(CTIME, time)
				.append(STATUS, 0);
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
	public boolean delete(String rid) throws DBException {
		updateStatus(rid, -1);
		return true;
	}

	private void updateStatus(String id, int status) throws DBException {
		if (StringUtils.isEmpty(id)) {
			throw new DBException("Insufficient parameters!");
		}

		DBObject query = QueryBuilder.start(_ID).is(id).get();
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
	public boolean updateUrl(String rid, String url) throws DBException {
		return false;
	}

	@Override
	public DocPo get(String rid) throws DBException {
		return null;
	}

	@Override
	public DocPo getUrl(String url) throws DBException {
		return null;
	}

	@Override
	public Paging<DocPo> list(int start, int length) throws DBException {
		List<DocPo> data = new ArrayList<DocPo>();
		return new Paging<DocPo>(data, count());
	}

	@Override
	public int count() throws DBException {
		return 0;
	}
}
