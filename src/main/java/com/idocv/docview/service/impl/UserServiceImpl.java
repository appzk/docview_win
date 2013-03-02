package com.idocv.docview.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.idocv.docview.dao.AppDao;
import com.idocv.docview.dao.UserDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.AppPo;
import com.idocv.docview.po.UserPo;
import com.idocv.docview.service.UserService;
import com.idocv.docview.vo.UserVo;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Resource
	private UserDao userDao;

	@Resource
	private AppDao appDao;

	@Override
	public UserVo signUp(String appKey, String username, String password, String email) throws DocServiceException {
		try {
			AppPo appPo = appDao.getByKey(appKey);
			if (null == appPo || StringUtils.isBlank(appPo.getId())) {
				throw new DocServiceException("App NOT found!");
			}
			if (isExistUsername(username)) {
				throw new DocServiceException("The username has been registered.");
			}
			if (isExistEmail(email)) {
				throw new DocServiceException("The email has been registered.");
			}
			UserPo po = userDao.signUp(appPo.getId(), username, password, email);
			return po2vo(po);
		} catch (DBException e) {
			logger.error("Sign up error: ", e);
			throw new DocServiceException("Sign up error: ", e);
		}
	}

	@Override
	public boolean isExistUsername(String username) throws DocServiceException {
		try {
			return userDao.isExistUsername(username);
		} catch (DBException e) {
			logger.error("isExistUsername error: ", e);
			throw new DocServiceException("isExistUsername error: ", e);
		}
	}

	@Override
	public boolean isExistEmail(String email) throws DocServiceException {
		try {
			return userDao.isExistEmail(email);
		} catch (DBException e) {
			logger.error("isExistEmail error: ", e);
			throw new DocServiceException("isExistEmail error: ", e);
		}
	}

	private UserVo po2vo(UserPo po) {
		UserVo vo = new UserVo();
		vo.setId(po.getId());
		vo.setApp(po.getApp());
		vo.setUsername(po.getUsername());
		vo.setPassword(po.getPassword());
		vo.setEmail(po.getEmail());
		vo.setCtiem(po.getCtiem());
		return vo;
	}
}