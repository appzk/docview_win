package com.idocv.docview.service;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.AppPo;

public interface AppService {

	boolean add(String id, String name, String key, String phone, String email) throws DocServiceException;
	
	AppPo get(String id) throws DocServiceException;

	AppPo getByKey(String key) throws DocServiceException;

}