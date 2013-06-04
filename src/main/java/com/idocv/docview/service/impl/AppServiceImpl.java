package com.idocv.docview.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.idocv.docview.dao.AppDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.AppPo;
import com.idocv.docview.service.AppService;
import com.idocv.docview.vo.AppVo;

@Service
public class AppServiceImpl implements AppService {

	private static final Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);
	
	@Resource
	private AppDao appDao;

	@Override
	public boolean add(String id, String name, String key, String phone, String email) throws DocServiceException {
		try {
			return appDao.add(id, name, key, phone, email);
		} catch (DBException e) {
			logger.error("Add app error: ", e);
			throw new DocServiceException(e);
		}
	}

	@Override
	public AppVo get(String id) throws DocServiceException {
		try {
			return convertPo2Vo(appDao.get(id));
		} catch (DBException e) {
			logger.error("get error: ", e);
			throw new DocServiceException(e);
		}
	}

	@Override
	public AppVo getByToken(String token) throws DocServiceException {
		try {
			return convertPo2Vo(appDao.getByToken(token));
		} catch (DBException e) {
			logger.error("getByKey error: ", e);
			throw new DocServiceException(e);
		}
	}
	
	private AppVo convertPo2Vo(AppPo po) {
		if (null == po) {
			return null;
		}
		AppVo vo = new AppVo();
		vo.setId(po.getId());
		vo.setName(po.getName());
		vo.setLogo(po.getLogo());
		vo.setKey(po.getKey());
		vo.setIps(po.getIps());
		vo.setPhone(po.getPhone());
		vo.setEmail(po.getEmail());
		vo.setAddress(po.getAddress());
		vo.setCtime(po.getCtime());
		vo.setUtime(po.getUtime());
		return vo;
	}
}
