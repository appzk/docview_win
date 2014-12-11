package com.idocv.test;

import java.util.HashMap;
import java.util.Map;

import com.idocv.docview.service.impl.ClusterServiceImpl;

public class Test {
	public static void main(String[] args) {
		String multiUrl = "node1@http://dfs1.idocv.com/doc/upload#node2@http://dfs2.idocv.com/doc/upload";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("node", "node2");
		System.out.println(params.get("node"));
		String node = (String) params.get("node");
		String nodeUrl = ClusterServiceImpl.getNodeUrl(multiUrl, node);
		System.out.println("node URL: " + nodeUrl);

	}
}