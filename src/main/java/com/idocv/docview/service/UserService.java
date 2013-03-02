package com.idocv.docview.service;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.vo.UserVo;

public interface UserService {
	
	/**
	 * Sign up
	 * 
	 * @param appKey app secret key
	 * @param username
	 * @param password
	 * @param email
	 * @return
	 * @throws DocServiceException
	 */
	UserVo signUp(String appKey, String username, String password, String email) throws DocServiceException;

	boolean isExistUsername(String username) throws DocServiceException;

	boolean isExistEmail(String email) throws DocServiceException;
}