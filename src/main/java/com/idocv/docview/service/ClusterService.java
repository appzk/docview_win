package com.idocv.docview.service;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.vo.DocVo;

public interface ClusterService {

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
	DocVo addUrl(String appId, String fileMd5, String ext, String node) throws DocServiceException;
}