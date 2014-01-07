package com.idocv.docview.service;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.vo.DocVo;

public interface ClusterService {

	public DocVo add(String fileName, byte[] data, String appid, String uid,
			String tid, String sid, int mode) throws DocServiceException;

	/**
	 * Asynchronously & instantly upload new file to DFS
	 * 
	 * @param uuid
	 */
	public void upload2DFSInstantly(String uuid);

	/**
	 * Upload all NEW file to DFS scheduled time
	 */
	public void upload2DFSBatchTask();

	/**
	 * Add remote file to local
	 * 
	 * @param appId
	 * @param fileMd5
	 * @param ext
	 * @return
	 * @throws DocServiceException
	 */
	DocVo addUrl(String appId, String fileMd5, String ext) throws DocServiceException;
}