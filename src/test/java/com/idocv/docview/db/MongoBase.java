package com.idocv.docview.db;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryBuilder;

public class MongoBase {
	protected static String dbHost = "localhost";
	protected static int dbPort = 27017;
	protected static String dbName = "test";
	protected static String collName = "test";
	protected static DBCollection coll;

	public static void init() {
		try {
			Mongo mongo = new Mongo(dbHost, dbPort);
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

		List<DBObject> objs = new ArrayList<DBObject>();
		objs.add(BasicDBObjectBuilder.start("age", 28).get());
		objs.add(BasicDBObjectBuilder.start().push("age").add("$exists", true).get());
		QueryBuilder query = QueryBuilder.start().or(objs.toArray(new DBObject[] {}));
		System.out.println("query: " + query.get());
		DBCursor cur = coll.find(query.get(), new BasicDBObject("_id", 1));
		List<DBObject> list = convertDBCursor2List(cur);
		printList(list);
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