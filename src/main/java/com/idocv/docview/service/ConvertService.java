package com.idocv.docview.service;

import java.util.concurrent.Callable;

import com.idocv.docview.exception.DocServiceException;

/**
 * Document convert task
 * 
 * @author Godwin
 * @since 2013-04-21
 * @version 1.0
 * 
 */
public interface ConvertService extends Callable<Boolean> {

	void convert(String rid) throws DocServiceException;

}