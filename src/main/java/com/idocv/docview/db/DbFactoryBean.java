package com.idocv.docview.db;

import org.springframework.beans.factory.FactoryBean;

import com.mongodb.DB;

public class DbFactoryBean implements FactoryBean<DB>{

	private AppMongoConn mongo;
	private String name;

	@Override
	public DB getObject() throws Exception {
		return mongo.getDB(name);
	}

	@Override
	public Class<?> getObjectType() {
		return DB.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	public void setMongo(AppMongoConn mongo) {
		this.mongo = mongo;
	}

	public void setName(String name) {
		this.name = name;
	}
}
