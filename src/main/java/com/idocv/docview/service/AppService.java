package com.idocv.docview.service;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.vo.AppVo;

public interface AppService {

	boolean add(String id, String name, String key, String phone, String email) throws DocServiceException;
	
	AppVo get(String id) throws DocServiceException;

	AppVo getByToken(String token) throws DocServiceException;

}