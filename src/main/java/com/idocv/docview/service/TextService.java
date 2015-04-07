package com.idocv.docview.service;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.WordVo;

/**
 * Office Text Service
 * 
 * @author Godwin
 * @since 2015-04-07
 * @version 1.0
 * 
 */
public interface TextService {

	/**
	 * Word page.
	 * 
	 * @param rid
	 * @param start
	 * @param limit
	 * @return
	 */
	PageVo<WordVo> getWordText(String rid, int start, int limit) throws DocServiceException;
}