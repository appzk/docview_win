package com.idocv.docview.service.impl;


import java.io.File;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import com.idocv.docview.util.UrlUtil;
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
	
	@Value("${upload.max.size}")
	private Long uploadMaxSize;

	@Value("${upload.max.msg}")
	private String uploadMaxMsg;

	private static Set<String> docTypes = new HashSet<String>();

	static {
		docTypes.add("doc");
		docTypes.add("docx");
		docTypes.add("xls");
		docTypes.add("xlsx");
		docTypes.add("ppt");
		docTypes.add("pptx");
		docTypes.add("pdf");
		docTypes.add("txt");
	}

	private static String authUrl = "http://www.idocv.com/auth.json";
	private static final ObjectMapper om = new ObjectMapper();
	private static final String macAddress = "52-54-00-BD-AD-F7";
	private static final boolean isCheckMacAddress = false;
	private static final boolean isCheckExpireDate = false;

	@Override
	public DocVo add(String app, String uid, String name, byte[] data, int mode, String labelName) throws DocServiceException {
		try {
			if (null == data || data.length <= 0) {
				logger.error("添加文件失败：数据为空！");
				throw new DocServiceException("添加文件失败：数据为空！");
			}
			if (data.length > uploadMaxSize) {
				logger.error(uploadMaxMsg);
				throw new DocServiceException(uploadMaxMsg);
			}

			// get app & user info
			if (StringUtils.isBlank(app)) {
				throw new DocServiceException("应用为空！");
			}
			
			// get label id
			String labelId = null;
			if (StringUtils.isNotBlank(labelName) && !"all".equalsIgnoreCase(labelName)) {
				LabelPo labelPo = labelDao.get(uid, labelName);
				if (null != labelPo) {
					labelId = labelPo.getId();
				}
			}
			return addDoc(app, uid, name, data, mode, labelId);
		} catch (DBException e) {
			logger.error("doc add error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public DocVo addUrl(String app, String uid, String name, String url, int mode, String labelName) throws DocServiceException {
		if (StringUtils.isBlank(app) || StringUtils.isBlank(url)) {
			logger.error("参数不足：app=" + app + ", uid=" + uid + ", url=" + url
					+ ", name=" + name + ", mode=" + mode);
			throw new DocServiceException(0, "请提供必要参数！");
		}
		try {
			DocVo vo = convertPo2Vo(docDao.getUrl(url, false));
			if (null != vo) {
				return vo;
			}
			
			if (StringUtils.isBlank(name) && url.contains(".") && url.matches(".*/[^/]+\\.[^/]+")) {
				name = url.replaceFirst(".*/([^/]+\\.[^/]+)", "$1");
			}
			
			byte[] data = null;
			if (StringUtils.isNotBlank(url) && url.matches("file:/{2,3}(.*)")) {
				// Local File
				String localPath = url.replaceFirst("file:/{2,3}(.*)", "$1");
				File srcFile = new File(localPath);
				if (!srcFile.isFile()) {
					logger.error("URL预览失败，未找到本地文件（" + localPath + "）");
					throw new DocServiceException("URL预览失败，未找到本地文件（"
							+ localPath + "）");
				}
				data = FileUtils.readFileToByteArray(srcFile);
			} else {
				// Web File
				String host = getHost(url);
				Response urlResponse = null;
				try {
					String encodedUrl = UrlUtil.encodeUrl(url);
					encodedUrl = encodedUrl.replaceAll(" ", "%20");
					// urlResponse = Jsoup.connect(url).referrer(host).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreContentType(true).execute();
					urlResponse = Jsoup.connect(encodedUrl).maxBodySize(uploadMaxSize.intValue() + 1000).referrer(host).timeout(60000).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreHttpErrors(true).followRedirects(true).ignoreContentType(true).execute();
					if (urlResponse.statusCode() == 307) {
						String sNewUrl = urlResponse.header("Location");
						if (sNewUrl != null && sNewUrl.length() > 7) {
							url = sNewUrl;
						}
						urlResponse = Jsoup.connect(encodedUrl).referrer(host).timeout(5000).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreHttpErrors(true).followRedirects(true).ignoreContentType(true).execute();
					}
					if (null == urlResponse) {
						throw new Exception("获取资源(" + url + ")时返回为空！");
					}
				} catch (Exception e) {
					logger.error("无法访问资源（" + url + "）：" + e.getMessage());
					throw new DocServiceException("无法访问资源（" + url + "）");
				}
				data = urlResponse.bodyAsBytes();
			}
			
			if (ArrayUtils.isEmpty(data)) {
				throw new DocServiceException("未找到可用的网络或本地文档！");
			}
			if (data.length > uploadMaxSize) {
				logger.error(uploadMaxMsg);
				throw new DocServiceException(uploadMaxMsg);
			}

			vo = add(app, null, name, data, mode, null);
			if (null == vo || StringUtils.isBlank(vo.getUuid())) {
				logger.error("save url doc error: 无法保存文件" + "app=" + app
						+ ", url=" + url + ", name=" + name + ", mode=" + mode);
				throw new DocServiceException("saveUrl error: 保存文件失败！");
			}
			docDao.updateUrl(vo.getUuid(), url);
			logger.info("[ADD URL ok]uuid=" + vo.getUuid() + ", url=" + url + ", name=" + name + ", size=" + data.length + ", app=" + app + ", uid=" + uid);
			return vo;
		} catch (Exception e) {
			logger.error("save url doc error: " + e.getMessage() + ", app=" + app + ", url=" + url + ", name=" + name + ", mode=" + mode);
			throw new DocServiceException("saveUrl error: " + e.getMessage(), e);
		}
	}

	private DocVo addDoc(String app, String uid, String name, byte[] data, int mode, String labelId) throws DocServiceException {
		if (!validateMacAddress(macAddress)) {
			System.out.println("[ERROR] This machine has NOT been authorized!");
			return null;
		}
		if (!validateExpireDate()) {
			System.out.println("[ERROR] This machine is expired!");
			return null;
		}
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
			String uuid = RcUtil.getUuidByRid(rid);
			String ext = RcUtil.getExt(rid);
			doc.setRid(rid);
			doc.setUuid(uuid);
			doc.setName(name);
			doc.setSize(size);
			doc.setCtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			if (0 == mode) {
				doc.setStatus(0);
			} else if (1 == mode) {
				doc.setStatus(1);
			}
			
			if (StringUtils.isBlank(ext) || !docTypes.contains(ext.toLowerCase())) {
				throw new DocServiceException(
						"暂不支持"
								+ ext
								+ "文件预览，请选择一个文档，支持格式：doc, docx, xls, xlsx, ppt, pptx和txt");
			}
			
			// save file meta and file
			FileUtils.writeByteArrayToFile(new File(rcUtil.getPath(rid)), data);

			// save info
			docDao.add(app, uid, rid, uuid, name, size, ext, mode, labelId);

			// Asynchronously convert document
			convertService.convert(rid);

			return convertPo2Vo(doc);
		} catch (Exception e) {
			logger.error("doc adddoc error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public boolean delete(String uuid) throws DocServiceException {
		try {
			return docDao.delete(uuid);
		} catch (DBException e) {
			logger.error("doc delete error: " + e.getMessage());
			throw new DocServiceException("delete doc error: ", e);
		}
	}

	@Override
	public void logView(String uuid) throws DocServiceException {
		try {
			docDao.logView(uuid);
		} catch (DBException e) {
			logger.error("doc logView error: " + e.getMessage());
			throw new DocServiceException("logView error: ", e);
		}
	}

	@Override
	public void logDownload(String uuid) throws DocServiceException {
		try {
			docDao.logDownload(uuid);
		} catch (DBException e) {
			logger.error("doc logDownload error: " + e.getMessage());
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
			logger.error("doc updateMode error: " + e.getMessage());
			throw new DocServiceException("updateMode error: ", e);
		}
	}

	@Override
	public DocVo get(String rid) throws DocServiceException {
		try {
			return convertPo2Vo(docDao.get(rid, false));
		} catch (DBException e) {
			logger.error("doc get error: " + e.getMessage());
			throw new DocServiceException("get doc error: ", e);
		}
	}

	@Override
	public DocVo getByUuid(String uuid) throws DocServiceException {
		try {
			return convertPo2Vo(docDao.getByUuid(uuid, false));
		} catch (DBException e) {
			logger.error("doc getByUuid error: " + e.getMessage());
			throw new DocServiceException("get doc error: ", e);
		}
	}

	@Override
	public DocVo getUrl(String url) throws DocServiceException {
		try {
			return convertPo2Vo(docDao.getUrl(url, false));
		} catch (DBException e) {
			logger.error("doc getUrl error: " + e.getMessage());
			throw new DocServiceException("getUrl doc error: ", e);
		}
	}

	@Override
	public Paging<DocVo> list(String app, String sid, int start, int length, String label, String search, QueryOrder queryOrder) throws DocServiceException {
		try {
			// Label
			// 1. all
			// 2. work
			// 3. personal
			// 4. other

			// 1. Check sid & get uid
			String uid = null;
			UserPo userPo = null;
			if (StringUtils.isNotBlank(sid)) {
				userPo = userDao.getBySid(sid);
				if (null != userPo) {
					uid = userPo.getId();
				}
			}

			// get label id
			String labelId = "all";
			if (StringUtils.isNotBlank(label) && !"all".equalsIgnoreCase(label)) {
				LabelPo labelPo = labelDao.get(uid, label);
				if (null != labelPo) {
					labelId = labelPo.getId();
				}
			}

			// get document list
			List<DocPo> docList;
			int count = 0;
			if (StringUtils.isNotBlank(uid) && null != userPo) {
				app = userPo.getAppId();
				if (100 == userPo.getStatus()) {// 管理员
					docList = docDao.listAppDocs(app, start, length, labelId,
							search, queryOrder, 0);
					count = docDao.countAppDocs(app, labelId, search, 0);
				} else { // 普通用户
					docList = docDao.listMyDocs(uid, start, length, labelId, search, queryOrder, 0);
					count = docDao.countMyDocs(uid, labelId, search, 0);
				}
			} else {
				docList = docDao.listAppDocs(app, start, length, labelId, search, queryOrder, 1);
				count = docDao.countAppDocs(app, labelId, search, 1);
			}
			return new Paging<DocVo>(convertPo2Vo(docList), count);
		} catch (DBException e) {
			logger.error("list doc error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	public long count(boolean includeDeleted) throws DocServiceException {
		try {
			return docDao.count(includeDeleted);
		} catch (DBException e) {
			logger.error("count doc error: " + e.getMessage());
			throw new DocServiceException("count doc error: " + e.getMessage());
		}
	}

	public static String getHost(String url) throws DocServiceException {
		return url.replaceFirst("((http[s]?)?(://))?([^/]*)(/?.*)", "$4");
	}

	private List<DocVo> convertPo2Vo(List<DocPo> poList) {
		List<DocVo> list = new ArrayList<DocVo>();
		if (CollectionUtils.isEmpty(poList)) {
			return list;
		}
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
		return vo;
	}
	
	private static boolean validateExpireDate() {
		if (!isCheckExpireDate) {
			return true;
		}
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(authUrl);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.out.println("[ERROR] Get expire status error(" + statusCode + ")");
				return false;
			}
			String response = method.getResponseBodyAsString();
			List<HashMap<String, String>> authMap = om.readValue(response,
					new TypeReference<List<HashMap<String, String>>>() {
					});
			for (HashMap<String, String> auth : authMap) {
				String mac = auth.get("mac");
				String expire = auth.get("expire");
				String valid = auth.get("valid");
				if (macAddress.equals(mac)) {
					Date expireDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expire);
					if (expireDate.after(new Date()) && "1".equals(valid)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("[ERROR] Get expire status error(" + e.getMessage() + ")");
			return false;
		} finally {
			method.releaseConnection();
		}
		return false;
	}
	
	private static boolean validateMacAddress(String macAddress) {
		if (!isCheckMacAddress) {
			return true;
		}
		if (StringUtils.isBlank(macAddress)) {
			return false;
		}
		List<String> macAddresses = getMacAddresses();
		if (macAddresses.contains(macAddress.toUpperCase())) {
			return true;
		} else {
			System.out.println("[ERROR] " + macAddress + " is NOT in " + macAddresses);
			return false;
		}
	}

	private static List<String> getMacAddresses() {
		List<String> macAddresses = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface ni = en.nextElement();
				byte[] mac = ni.getHardwareAddress();
				StringBuilder sb = new StringBuilder();
				if (ArrayUtils.isEmpty(mac)) {
					continue;
				}
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
				}
				macAddresses.add(sb.toString().toUpperCase());
			}
			return macAddresses;
		} catch (Exception e) {
			return macAddresses;
		}
	}
}
