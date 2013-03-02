package com.idocv.docview.service.impl;

import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
			UserVo vo = getByUsername(username);
			if (null != vo && StringUtils.isNotBlank(vo.getId())) {
				throw new DocServiceException("The username has been registered.");
			}
			vo = getByEmail(email);
			if (null != vo && StringUtils.isNotBlank(vo.getId())) {
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
	public UserVo login(String user, String password) throws DocServiceException {
		try {
			if (StringUtils.isBlank(user) || StringUtils.isBlank(password)) {
				throw new DocServiceException("Insufficient parameters!");
			}
			UserVo vo = getByEmail(user);
			if (null == vo) {
				vo = getByUsername(user);
			}
			if (null == vo) {
				throw new DocServiceException("User NOT found!");
			}
			if (!password.equals(vo.getPassword())) {
				throw new DocServiceException("Password ERROR!");
			}
			String sid = userDao.addSid(vo.getId());
			vo.setSid(sid);
			return vo;
		} catch (DBException e) {
			logger.error("Sign up error: ", e);
			throw new DocServiceException("Sign up error: ", e);
		}
	}

	@Override
	public UserVo getByUsername(String username) throws DocServiceException {
		try {
			return po2vo(userDao.getByUsername(username));
		} catch (DBException e) {
			logger.error("getByUsername error: ", e);
			throw new DocServiceException("getByUsername error: ", e);
		}
	}

	@Override
	public UserVo getByEmail(String email) throws DocServiceException {
		try {
			return po2vo(userDao.getByEmail(email));
		} catch (DBException e) {
			logger.error("getByEmail error: ", e);
			throw new DocServiceException("getByEmail error: ", e);
		}
	}

	@Override
	public UserVo getBySid(String sid) throws DocServiceException {
		try {
			return po2vo(userDao.getBySid(sid));
		} catch (DBException e) {
			logger.error("getBySid error: ", e);
			throw new DocServiceException("getBySid error: ", e);
		}
	}

	private UserVo po2vo(UserPo po) {
		if (null == po) {
			return null;
		}
		UserVo vo = new UserVo();
		vo.setId(po.getId());
		vo.setApp(po.getAppId());
		vo.setUsername(po.getUsername());
		vo.setPassword(po.getPassword());
		vo.setEmail(po.getEmail());
		vo.setCtime(po.getCtime());
		Collection<String> sids = po.getSids();
		if (!CollectionUtils.isEmpty(sids)) {
			String[] sidsArray = sids.toArray(new String[0]);
			vo.setSid(sidsArray[sidsArray.length - 1]);
		}
		return vo;
	}
}