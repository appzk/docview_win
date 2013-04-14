package com.idocv.docview.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.dao.LabelDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.LabelPo;
import com.idocv.docview.service.LabelService;
import com.idocv.docview.vo.LabelVo;

@Service
public class LabelServiceImpl implements LabelService {
	
	private static final Logger logger = LoggerFactory.getLogger(LabelServiceImpl.class);

	@Resource
	private LabelDao labelDao;

	@Override
	public void initUser(String uid) throws DocServiceException {
		try {
			Map<String, String> labels = new HashMap<String, String>();
			labels.put("personal", "个人");
			labels.put("work", "工作");
			for (Entry<String, String> label : labels.entrySet()) {
				labelDao.add(uid, label.getKey(), label.getValue());
			}
		} catch (Exception e) {
			logger.error("Init user labels error: ", e);
			throw new DocServiceException(e);
		}
	}

	@Override
	public LabelVo add(String uid, String name, String value) throws DocServiceException {
		if (StringUtils.isBlank(uid)) {
			throw new DocServiceException("UID为空！");
		}
		if (StringUtils.isBlank(name)) {
			throw new DocServiceException("标签称为空！");
		}
		try {
			LabelPo po = labelDao.add(uid, name, value);
			return convertPo2Vo(po);
		} catch (Exception e) {
			logger.error("save doc error: ", e);
			throw new DocServiceException(e);
		}
	}

	@Override
	public boolean delete(String id) throws DocServiceException {
		try {
			return labelDao.delete(id);
		} catch (DBException e) {
			logger.error("delete label error: ", e);
			throw new DocServiceException("delete label error: ", e);
		}
	}

	@Override
	public LabelVo get(String id, boolean includeDeleted) throws DocServiceException {
		try {
			return convertPo2Vo(labelDao.get(id, false));
		} catch (DBException e) {
			logger.error("get label error: ", e);
			throw new DocServiceException("get label error: ", e);
		}
	}

	@Override
	public List<LabelVo> list(String uid) throws DocServiceException {
		try {
			List<LabelPo> poList = labelDao.list(uid);
			if (CollectionUtils.isEmpty(poList)) {
				// Init User labels
				initUser(uid);
				poList = labelDao.list(uid);
			}
			return convertPo2Vo(poList);
		} catch (DBException e) {
			logger.error("list label error: ", e);
			throw new DocServiceException("list label error: ", e);
		}
	}

	private List<LabelVo> convertPo2Vo(List<LabelPo> poList) {
		if (CollectionUtils.isEmpty(poList)) {
			return null;
		}
		List<LabelVo> list = new ArrayList<LabelVo>();
		for (LabelPo po : poList) {
			list.add(convertPo2Vo(po));
		}
		return list;
	}

	private LabelVo convertPo2Vo(LabelPo po) {
		if (null == po) {
			return null;
		}
		LabelVo vo = new LabelVo();
		vo.setId(po.getId());
		vo.setUid(po.getUid());
		vo.setName(po.getName());
		vo.setValue(po.getValue());
		vo.setStatus(po.getStatus());
		vo.setCtime(po.getCtime());
		vo.setUtime(po.getUtime());
		return vo;
	}
}