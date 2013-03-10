package com.idocv.docview.service.impl;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import com.idocv.docview.exception.DBException;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.AppPo;
import com.idocv.docview.po.DocPo;
import com.idocv.docview.service.DocService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;


@Service
public class DocServiceImpl implements DocService {
	
	private static final Logger logger = LoggerFactory.getLogger(DocServiceImpl.class);

	@Resource
	private AppDao appDao;

	@Resource
	private DocDao docDao;

	@Resource
	private RcUtil rcUtil;

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
	public DocVo add(String appKey, String name, byte[] data) throws DocServiceException {
		if (StringUtils.isBlank(appKey) || StringUtils.isBlank(name) || null == data || data.length <= 0) {
			throw new DocServiceException(0, "Insufficient parameter!");
		}
		try {
			AppPo appPo = appDao.getByKey(appKey);
			if (null == appPo || StringUtils.isBlank(appPo.getId())) {
				throw new DocServiceException(0, "Application NOT found!");
			}
			String appId = appPo.getId();
			DocPo doc = new DocPo();
			int size = data.length;
			String rid = RcUtil.genRid(appId, name, size);
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
			doc.setCtime(System.currentTimeMillis());
			
			// save file meta and file
			FileUtils.writeByteArrayToFile(new File(rcUtil.getPath(rid)), data);

			// save info
			docDao.add(rid, uuid, appId, name, size, ext);
			return convertPo2Vo(doc);
		} catch (Exception e) {
			logger.error("save doc error: ", e);
			throw new DocServiceException(e);
		}
	}

	@Override
	public DocVo addUrl(String appKey, String url, String name) throws DocServiceException {
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
			vo = add(appKey, name, bytes);
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
			AppPo appPo = appDao.getByKey(token);
			if (null == appPo) {
				throw new DocServiceException("App NOT found!");
			}
			DocVo docVo = getByUuid(uuid);
			if (null == docVo) {
				throw new DocServiceException("Document NOT found!");
			}
			String appAppId = appPo.getId();
			String docAppId = docVo.getAppId();
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
	public Paging<DocVo> list(int start, int length) throws DocServiceException {
		try {
			return new Paging<DocVo>(convertPo2Vo(docDao.list(start, length)), (int) count(false));
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
		vo.setAppId(po.getAppId());
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
