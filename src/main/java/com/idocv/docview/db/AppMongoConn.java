package com.idocv.docview.db;

import java.net.UnknownHostException;

import org.springframework.beans.factory.DisposableBean;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;

public class AppMongoConn implements DisposableBean{
	private String host;
	private int port;

	private MongoClient mongo;

	public AppMongoConn(String host, int port) throws UnknownHostException, MongoException {
		this.host = host;
		this.port = port;
		mongo = new MongoClient(host, port);
		MongoClientOptions mongoOptions = MongoClientOptions.builder()
				.connectionsPerHost(300)
				.threadsAllowedToBlockForConnectionMultiplier(8000)
				.connectTimeout(30000).build();
		int options = mongo.getOptions();
		System.err.println("##[MONGO INFO] mongoOptions: " + options + " - " + mongoOptions);
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
