package com.idocv.docview.service.impl;

import java.net.URLEncoder;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
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
import com.idocv.docview.util.EmailUtil;
import com.idocv.docview.vo.UserVo;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Resource
	private UserDao userDao;

	@Resource
	private AppDao appDao;

	@Override
	public UserVo add(String token, String username, String password, String email) throws DocServiceException {
		try {
			AppPo appPo = appDao.getByToken(token);
			if (null == appPo || StringUtils.isBlank(appPo.getId())) {
				throw new DocServiceException("不存在该应用！");
			}
			UserVo vo = getByUsername(username);
			if (null != vo && StringUtils.isNotBlank(vo.getId())) {
				throw new DocServiceException("该用户已被注册，请选择其他用户名或直接登录！");
			}
			vo = getByEmail(email);
			if (null != vo && StringUtils.isNotBlank(vo.getId())) {
				throw new DocServiceException("该邮箱已被注册，请选择其他邮箱或直接登录！");
			}
			UserPo po = userDao.add(appPo.getId(), username, password, email);

			// Send email
			String encodeEmail = URLEncoder.encode(email, "UTF-8");
			String title = "邮箱验证";
			String key = getActivationKey(po.getId());
			String link = "<a href=\"http://user.idocv.com/activate?email=" + encodeEmail + "&key=" + key + "\">验证邮箱</a>";
			String content = "请点击链接以验证您的账号：" + link;
			content += "<br />或复制以下链接到地址栏以验证您的账号：<br />";
			content += "http://user.idocv.com/activate?email=" + encodeEmail + "&key=" + key;
			EmailUtil.sendMail(username, email, title, content);

			return po2vo(po);
		} catch (Exception e) {
			logger.error("Sign up error: ", e);
			throw new DocServiceException("注册失败：" + e.getMessage(), e);
		}
	}

	@Override
	public UserVo activate(String email, String key) throws DocServiceException {
		try {
			if (StringUtils.isBlank(email) || StringUtils.isBlank(key)) {
				logger.error("请提供必要参数：email=" + email + ", key=" + key);
				throw new DBException("请提供必要参数");
			}
			UserPo userPo = userDao.getByEmail(email);
			if (null == userPo) {
				throw new DocServiceException("用户（" + email + "）不存在！");
			}
			if (!key.equals(getActivationKey(userPo.getId()))) {
				logger.error("激活码错误：email=" + email + ", key=" + key);
				throw new DBException("激活码错误！");
			}
			if (userPo.getStatus() > 0) {
				throw new DBException("该用户已被激活！");
			}
			userDao.updateStatusByEmail(email, 1);
			UserVo vo = getByEmail(email);
			return vo;
		} catch (DBException e) {
			logger.error("activate error: ", e);
			throw new DocServiceException("验证邮箱失败：" + e.getMessage(), e);
		}
	}

	@Override
	public UserVo login(String user, String password) throws DocServiceException {
		try {
			if (StringUtils.isBlank(user) || StringUtils.isBlank(password)) {
				logger.error("登录失败：请填写用户名和密码, user=" + user + ", password=" + password);
				throw new DocServiceException("请填写用户名和密码！");
			}
			UserVo vo = getByEmail(user);
			if (null == vo) {
				vo = getByUsername(user);
			}
			if (null == vo) {
				logger.error("登录失败：用户（" + user + "）不存在！");
				throw new DocServiceException("用户（" + user + "）不存在！");
			}
			if (!password.equals(vo.getPassword())) {
				throw new DocServiceException("密码错误！");
			}
			if (vo.getStatus() < 1) {
				throw new DocServiceException("您还未验证邮箱，请先到您的邮箱激活账号！");
			}
			String sid = userDao.addSid(vo.getId());
			vo.setSid(sid);
			return vo;
		} catch (DBException e) {
			logger.error("Sign up error: ", e);
			throw new DocServiceException("登录失败：" + e.getMessage(), e);
		}
	}

	@Override
	public void logout(String sid) throws DocServiceException {
		try {
			if (StringUtils.isBlank(sid)) {
				throw new DocServiceException("Insufficient parameters!");
			}
			userDao.logout(sid);
		} catch (DBException e) {
			logger.error("Logout error: ", e);
			throw new DocServiceException("Logout error: ", e);
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
			throw new DocServiceException(e.getMessage(), e);
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
		vo.setStatus(po.getStatus());
		Collection<String> sids = po.getSids();
		if (!CollectionUtils.isEmpty(sids)) {
			String[] sidsArray = sids.toArray(new String[0]);
			vo.setSid(sidsArray[sidsArray.length - 1]);
		}
		return vo;
	}

	private static String getActivationKey(String uid) {
		return DigestUtils.shaHex(uid);
	}
}