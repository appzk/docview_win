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

	/**
	 * login
	 * 
	 * @param user
	 * @param password
	 * @return
	 * @throws DocServiceException
	 */
	UserVo login(String user, String password) throws DocServiceException;

	UserVo getByUsername(String username) throws DocServiceException;

	UserVo getByEmail(String email) throws DocServiceException;

	UserVo getBySid(String sid) throws DocServiceException;
}