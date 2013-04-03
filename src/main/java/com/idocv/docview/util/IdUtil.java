package com.idocv.docview.util;

import org.bson.types.ObjectId;

public class IdUtil {
	public static String getObjectId() {
		return new ObjectId().toString();
	}
}