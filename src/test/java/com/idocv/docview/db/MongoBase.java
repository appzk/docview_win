package com.idocv.docview.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;

public class MongoBase {
	protected static String dbHost = "localhost";
	protected static int dbPort = 27017;
	protected static String dbName = "test";
	protected static String collName = "test";
	protected static DBCollection coll;

	public static void init() {
		try {
			MongoClient mongo = new MongoClient(dbHost, dbPort);
			DB db = mongo.getDB(dbName);
			coll = db.getCollection(collName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		init();
		coll.insert(BasicDBObjectBuilder.start("_id", "aaa").add("name", "Godwin").add("age", 28).get());
		coll.insert(BasicDBObjectBuilder.start("_id", "bbb").add("name", "Gao").add("age", 29).get());
		coll.insert(BasicDBObjectBuilder.start("_id", "ccc").add("name", "Congying").add("age", 25).get());

		QueryBuilder query = QueryBuilder.start("_id").is("aaa");
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start().push("$set").append("utime", "uuuuu");
		if (StringUtils.isBlank(null)) {
			builder.pop().push("$unset").append("name", 1);
		} else {
			builder.append("name", "NNNNNN");
		}
		coll.update(query.get(), builder.get(), true, true);
	}

	public static List<DBObject> list() {
		DBCursor cur = coll.find();
		return convertDBCursor2List(cur);
	}

	public static List<DBObject> convertDBCursor2List(DBCursor cur) {
		if (null == cur) {
			return null;
		}
		List<DBObject> poList = new ArrayList<DBObject>();
		while (cur.hasNext()) {
			DBObject obj = cur.next();
			poList.add(obj);
		}
		return poList;
	}

	public static void printList(List<DBObject> list) {
		if (null != list) {
			for (DBObject obj : list) {
				System.out.println(obj);
			}
		}
	}
}