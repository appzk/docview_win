package com.idocv.docview.service.impl;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.common.Paging;
import com.idocv.docview.dao.AppDao;
import com.idocv.docview.dao.DocDao;
import com.idocv.docview.dao.DocDao.QueryOrder;
import com.idocv.docview.dao.LabelDao;
import com.idocv.docview.dao.UserDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.AppPo;
import com.idocv.docview.po.DocPo;
import com.idocv.docview.po.LabelPo;
import com.idocv.docview.po.UserPo;
import com.idocv.docview.service.ConvertService;
import com.idocv.docview.service.DocService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;


@Service
public class DocServiceImpl implements DocService {
	
	private static final Logger logger = LoggerFactory.getLogger(DocServiceImpl.class);

	@Resource
	private AppDao appDao;

	@Resource
	private UserDao userDao;

	@Resource
	private DocDao docDao;

	@Resource
	private LabelDao labelDao;

	@Resource
	private RcUtil rcUtil;

	@Resource
	private ConvertService convertService;

	private static Set<String> docTypes = new HashSet<String>();

	static {
		docTypes.add("doc");
		docTypes.add("docx");
		docTypes.add("odt");	// OpenOffice Writer
		docTypes.add("xls");
		docTypes.add("xlsx");
		docTypes.add("ods");	// OpenOffice Spreadsheet
		docTypes.add("ppt");
		docTypes.add("pptx");
		docTypes.add("odp");	// OpenOffice Presentation
		docTypes.add("pdf");
		docTypes.add("txt");
	}

	@Override
	public DocVo addByApp(String token, String name, byte[] data, int mode) throws DocServiceException {
		if (StringUtils.isBlank(token)) {
			throw new DocServiceException(0, "token为空！");
		}
		try {
			AppPo appPo = appDao.getByToken(token);
			if (null == appPo || StringUtils.isBlank(appPo.getId())) {
				logger.error("不存在该应用，token=" + token);
				throw new DocServiceException(0, "不存在该应用！");
			}
			String app = appPo.getId();
			return addDoc(app, null, name, data, mode, null);
		} catch (Exception e) {
			logger.error("save doc error: ", e);
			throw new DocServiceException(e);
		}
	}

	@Override
	public DocVo addByUser(String sid, String name, byte[] data, int mode, String labelName) throws DocServiceException {
		try {
			// get app & user info
			if (StringUtils.isBlank(sid)) {
				throw new DocServiceException("用户sid为空！");
			}
			UserPo userPo = userDao.getBySid(sid);
			if (null == userPo) {
				throw new DocServiceException("用户未找到！");
			}
			String app = userPo.getAppId();
			String uid = userPo.getId();
			
			// get label id
			String labelId = null;
			if (StringUtils.isNotBlank(labelName) && "all".equalsIgnoreCase(labelName)) {
				LabelPo labelPo = labelDao.get(uid, labelName);
				if (null != labelPo) {
					labelId = labelPo.getId();
				}
			}
			return addDoc(app, uid, name, data, mode, labelId);
		} catch (DBException e) {
			logger.error("save doc by user error: ", e);
			throw new DocServiceException(e);
		}
	}

	private DocVo addDoc(String app, String uid, String name, byte[] data, int mode, String labelId) throws DocServiceException {
		if (StringUtils.isBlank(app)) {
			throw new DocServiceException(0, "应用为空！");
		}
		if (StringUtils.isBlank(name)) {
			throw new DocServiceException(0, "文件名为空！");
		}
		if (null == data) {
			throw new DocServiceException(0, "请选择一个文档！");
		}
		if (data.length <= 0) {
			throw new DocServiceException(0, "不能上传空文档！");
		}
		try {
			DocPo doc = new DocPo();
			int size = data.length;
			String rid = RcUtil.genRid(app, name, size);
			String uuid = RandomStringUtils.randomAlphanumeric(5);
			doc.setRid(rid);
			String ext = RcUtil.getExt(rid);
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)) {
				uuid += "w";
			} else if ("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext)) {
				uuid += "x";
			} else if ("ppt".equalsIgnoreCase(ext) || "pptx".equalsIgnoreCase(ext)) {
				uuid += "p";
			} else if ("txt".equalsIgnoreCase(ext)) {
				uuid += "t";
			}
			doc.setUuid(uuid);
			doc.setName(name);
			doc.setSize(size);
			doc.setCtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			doc.setMode(mode);
			
			// save file meta and file
			FileUtils.writeByteArrayToFile(new File(rcUtil.getPath(rid)), data);

			// save info
			docDao.add(app, uid, rid, uuid, name, size, ext, mode, labelId);

			// Asynchronously convert document
			convertService.convert(rid);

			return convertPo2Vo(doc);
		} catch (Exception e) {
			logger.error("save doc error: ", e);
			throw new DocServiceException(e);
		}
	}
	
	@Override
	public DocVo addUrl(String appKey, String url, String name, int mode) throws DocServiceException {
		if (StringUtils.isBlank(appKey) || StringUtils.isBlank(name) || StringUtils.isBlank(url)) {
			throw new DocServiceException(0, "Insufficient parameter!");
		}
		try {
			DocVo vo = convertPo2Vo(docDao.getUrl(url));
			if (null != vo) {
				return vo;
			}
			String host = getHost(url);
			Response urlResponse = null;
			urlResponse = Jsoup.connect(url).referrer(host).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreContentType(true).execute();
			byte[] bytes = urlResponse.bodyAsBytes();
			vo = addByApp(appKey, name, bytes, mode);
			docDao.updateUrl(vo.getRid(), url);
			return vo;
		} catch (IOException e) {
			logger.error("save url doc error: ", e);
			throw new DocServiceException("saveUrl error: ", e);
		} catch (DBException e) {
			logger.error("save url doc error: ", e);
			throw new DocServiceException("saveUrl error: ", e);
		}
	}

	@Override
	public boolean delete(String uuid) throws DocServiceException {
		try {
			return docDao.delete(uuid);
		} catch (DBException e) {
			logger.error("delete doc error: ", e);
			throw new DocServiceException("delete doc error: ", e);
		}
	}

	@Override
	public void logView(String uuid) throws DocServiceException {
		try {
			docDao.logView(uuid);
		} catch (DBException e) {
			logger.error("logView error: ", e);
			throw new DocServiceException("logView error: ", e);
		}
	}

	@Override
	public void logDownload(String uuid) throws DocServiceException {
		try {
			docDao.logDownload(uuid);
		} catch (DBException e) {
			logger.error("logDownload error: ", e);
			throw new DocServiceException("logDownload error: ", e);
		}
	}

	@Override
	public void updateMode(String token, String uuid, int mode) throws DocServiceException {
		try {
			AppPo appPo = appDao.getByToken(token);
			if (null == appPo) {
				throw new DocServiceException("App NOT found!");
			}
			DocVo docVo = getByUuid(uuid);
			if (null == docVo) {
				throw new DocServiceException("Document NOT found!");
			}
			String appAppId = appPo.getId();
			String docAppId = docVo.getApp();
			if (!appAppId.equals(docAppId)) {
				throw new DocServiceException("Can NOT modify other app's document.");
			}
			docDao.updateMode(uuid, mode);
		} catch (DBException e) {
			logger.error("updateMode error: ", e);
			throw new DocServiceException("updateMode error: ", e);
		}
	}

	@Override
	public DocVo get(String rid) throws DocServiceException {
		try {
			return convertPo2Vo(docDao.get(rid, false));
		} catch (DBException e) {
			logger.error("get doc error: ", e);
			throw new DocServiceException("get doc error: ", e);
		}
	}

	@Override
	public DocVo getByUuid(String uuid) throws DocServiceException {
		try {
			return convertPo2Vo(docDao.getByUuid(uuid, false));
		} catch (DBException e) {
			logger.error("get doc error: ", e);
			throw new DocServiceException("get doc error: ", e);
		}
	}

	@Override
	public DocVo getUrl(String url) throws DocServiceException {
		try {
			return convertPo2Vo(docDao.getUrl(url));
		} catch (DBException e) {
			logger.error("getUrl doc error: ", e);
			throw new DocServiceException("getUrl doc error: ", e);
		}
	}

	@Override
	public Paging<DocVo> list(String uid, int start, int length, String label, String search, QueryOrder queryOrder) throws DocServiceException {
		try {
			// Label
			// 1. all
			// 2. work
			// 3. personal
			// 4. other
			String labelId = "all";
			if (StringUtils.isNotBlank(label) && !"all".equalsIgnoreCase(label)) {
				LabelPo labelPo = labelDao.get(uid, label);
				if (null != labelPo) {
					labelId = labelPo.getId();
				}
			}
			// TODO remove this line afterwards
			uid = null;
			return new Paging<DocVo>(convertPo2Vo(docDao.listMyDocs(uid, start, length, labelId, search, queryOrder)), (int) count(false));
		} catch (DBException e) {
			logger.error("list doc error: ", e);
			throw new DocServiceException("list doc error: ", e);
		}
	}

	@Override
	public long count(boolean includeDeleted) throws DocServiceException {
		try {
			return docDao.count(includeDeleted);
		} catch (DBException e) {
			logger.error("count doc error: ", e);
			throw new DocServiceException("count doc error: ", e);
		}
	}

	public static String getHost(String url) throws DocServiceException {
		return url.replaceFirst("((http[s]?)?(://))?([^/]*)(/?.*)", "$4");
	}

	private List<DocVo> convertPo2Vo(List<DocPo> poList) {
		if (CollectionUtils.isEmpty(poList)) {
			return null;
		}
		List<DocVo> list = new ArrayList<DocVo>();
		for (DocPo po : poList) {
			list.add(convertPo2Vo(po));
		}
		return list;
	}
	
	private DocVo convertPo2Vo(DocPo po) {
		if (null == po) {
			return null;
		}
		DocVo vo = new DocVo();
		vo.setRid(po.getRid());
		vo.setUuid(po.getUuid());
		vo.setApp(po.getApp());
		vo.setName(po.getName());
		vo.setSize(po.getSize());
		vo.setStatus(po.getStatus());
		vo.setCtime(po.getCtime());
		vo.setUtime(po.getUtime());
		vo.setExt(po.getExt());
		vo.setUrl(po.getUrl());
		if (!CollectionUtils.isEmpty(po.getViewLog())) {
			vo.setViewCount(po.getViewLog().size());
		}
		if (!CollectionUtils.isEmpty(po.getDownloadLog())) {
			vo.setDownloadCount(po.getDownloadLog().size());
		}
		vo.setMode(po.getMode());
		return vo;
	}
}
