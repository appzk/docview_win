package com.idocv.test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryBuilder;

public class MongoTest {

	private static String dbHost = "localhost";
	private static int dbPort = 27017;
	private static String dbName = "test";
	private static String collName = "test";
	private static DBCollection coll;

	public static void main(String[] args) {
		try {
			init();
			String id = new ObjectId().toString();
			DBObject obj1 = BasicDBObjectBuilder.start("_id", new ObjectId().toString()).add("name", "Godwin").get();
			DBObject obj2 = BasicDBObjectBuilder.start("_id", new ObjectId().toString()).add("name", "Hello").get();
			DBObject obj3 = BasicDBObjectBuilder.start("_id", new ObjectId().toString()).add("name", "World").get();
			// coll.save(obj1);
			// coll.save(obj2);
			// coll.save(obj3);

			QueryBuilder query = QueryBuilder.start("name").regex(
					Pattern.compile("godwin", Pattern.CASE_INSENSITIVE));

			System.err.println("/*** list ***/");
			List<DBObject> list = convertDBCursor2List(coll.find(query.get()));
			for (DBObject obj : list) {
				System.out.println(obj);
			}
			System.err.println("Done!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void init() {
		try {
			Mongo mongo = new Mongo(dbHost, dbPort);
			DB db = mongo.getDB(dbName);
			coll = db.getCollection(collName);
		} catch (Exception e) {
			e.printStackTrace();
		}
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