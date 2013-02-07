package com.idocv.docview.db;

import java.net.UnknownHostException;

import org.springframework.beans.factory.DisposableBean;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class AppMongoConn implements DisposableBean{
	private String host;
	private int port;

	Mongo mongo;

	public AppMongoConn(String host, int port) throws UnknownHostException, MongoException {
		this.host = host;
		this.port = port;
		mongo = new Mongo(host, port);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public DB getDB(String name) {
		return mongo.getDB(name);
	}

	@Override
	public void destroy() throws Exception {
		if (mongo != null) {
			mongo.close();
		}
	}
}
