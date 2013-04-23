package com.idocv.docview.service.impl;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.dao.AppDao;
import com.idocv.docview.dao.SessionDao;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.AppPo;
import com.idocv.docview.po.SessionPo;
import com.idocv.docview.service.SessionService;
import com.idocv.docview.vo.SessionVo;


@Service
public class SessionServiceImpl implements SessionService {
	
	private static final Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class);

	@Resource
	private AppDao appDao;

	@Resource
	private SessionDao sessionDao;

	@Override
	public String add(String appKey, String uuid) throws DocServiceException {
		if (StringUtils.isBlank(appKey) || StringUtils.isBlank(uuid)) {
			throw new DocServiceException(0, "Insufficient parameter!");
		}
		try {
			AppPo appPo = appDao.getByToken(appKey);
			if (null == appPo || StringUtils.isBlank(appPo.getId())) {
				throw new DocServiceException(0, "Application NOT found!");
			}
			String appId = appPo.getId();
			return sessionDao.add(appId, uuid);
		} catch (Exception e) {
			logger.error("add session error: ", e);
			throw new DocServiceException(e);
		}
	}

	@Override
	public SessionVo get(String id) throws DocServiceException {
		if (StringUtils.isBlank(id)) {
			throw new DocServiceException(0, "Insufficient parameter!");
		}
		try {
			SessionPo po = sessionDao.get(id);
			if (null == po) {
				throw new DocServiceException("session NOT found!");
			}
			return convertPo2Vo(po);
		} catch (Exception e) {
			logger.error("get session error: ", e);
			throw new DocServiceException(e);
		}
	}

	private List<SessionVo> convertPo2Vo(List<SessionPo> poList) {
		if (CollectionUtils.isEmpty(poList)) {
			return null;
		}
		List<SessionVo> list = new ArrayList<SessionVo>();
		for (SessionPo po : poList) {
			list.add(convertPo2Vo(po));
		}
		return list;
	}

	private SessionVo convertPo2Vo(SessionPo po) {
		if (null == po) {
			return null;
		}
		SessionVo vo = new SessionVo();
		vo.setId(po.getId());
		vo.setAppId(po.getAppId());
		vo.setUuid(po.getUuid());
		vo.setCtime(po.getCtime());
		return vo;
	}
}