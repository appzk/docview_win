package com.idocv.docview.dao.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.idocv.docview.dao.AppDao;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;

@Repository
public class AppDaoImpl extends BaseDaoImpl implements AppDao, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		if (null != db) {
			DBCollection coll;
			// MailPo index: pmid, (pmid:ctime)
			coll = db.getCollection(COLL_APP);
			coll.ensureIndex(BasicDBObjectBuilder.start().add(PMID, 1).get());

		}

	}

}
