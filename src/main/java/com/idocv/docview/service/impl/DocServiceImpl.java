package com.idocv.docview.service.impl;


import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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

import com.idocv.docview.common.Paging;
import com.idocv.docview.dao.AppDao;
import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.AppPo;
import com.idocv.docview.po.DocPo;
import com.idocv.docview.service.DocService;
import com.idocv.docview.util.RcUtil;


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
	public DocPo add(String appKey, String name, byte[] data) throws DocServiceException {
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
			doc.setUuid(uuid);
			String ext = RcUtil.getExt(rid);
			doc.setName(name);
			doc.setSize(size);
			doc.setCtime(System.currentTimeMillis());
			
			// save file meta and file
			FileUtils.writeByteArrayToFile(new File(rcUtil.getPath(rid)), data);

			// save info
			docDao.add(rid, uuid, appId, name, size, ext);
			return doc;
		} catch (Exception e) {
			logger.error("save doc error: ", e);
			throw new DocServiceException(e);
		}
	}

	@Override
	public DocPo addUrl(String appKey, String url, String name) throws DocServiceException {
		if (StringUtils.isBlank(appKey) || StringUtils.isBlank(name) || StringUtils.isBlank(url)) {
			throw new DocServiceException(0, "Insufficient parameter!");
		}
		try {
			DocPo po = docDao.getUrl(url);
			if (null != po) {
				return po;
			}
			String host = getHost(url);
			Response urlResponse = null;
			urlResponse = Jsoup.connect(url).referrer(host).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreContentType(true).execute();
			byte[] bytes = urlResponse.bodyAsBytes();
			po = add(appKey, name, bytes);
			docDao.updateUrl(po.getRid(), url);
			return po;
		} catch (IOException e) {
			logger.error("save url doc error: ", e);
			throw new DocServiceException("saveUrl error: ", e);
		} catch (DBException e) {
			logger.error("save url doc error: ", e);
			throw new DocServiceException("saveUrl error: ", e);
		}
	}

	@Override
	public boolean delete(String rid) throws DocServiceException {
		try {
			return docDao.delete(rid);
		} catch (DBException e) {
			logger.error("delete doc error: ", e);
			throw new DocServiceException("delete doc error: ", e);
		}
	}

	@Override
	public DocPo get(String rid) throws DocServiceException {
		try {
			return docDao.get(rid, false);
		} catch (DBException e) {
			logger.error("get doc error: ", e);
			throw new DocServiceException("get doc error: ", e);
		}
	}

	@Override
	public DocPo getByUuid(String uuid) throws DocServiceException {
		try {
			return docDao.getByUuid(uuid, false);
		} catch (DBException e) {
			logger.error("get doc error: ", e);
			throw new DocServiceException("get doc error: ", e);
		}
	}

	@Override
	public DocPo getUrl(String url) throws DocServiceException {
		try {
			return docDao.getUrl(url);
		} catch (DBException e) {
			logger.error("getUrl doc error: ", e);
			throw new DocServiceException("getUrl doc error: ", e);
		}
	}

	@Override
	public Paging<DocPo> list(int start, int length) throws DocServiceException {
		try {
			return new Paging<DocPo>(docDao.list(start, length), (int) count(false));
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
}
