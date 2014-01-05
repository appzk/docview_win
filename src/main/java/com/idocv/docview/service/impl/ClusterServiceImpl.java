package com.idocv.docview.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.DocPo;
import com.idocv.docview.service.ClusterService;
import com.idocv.docview.service.ConvertService;
import com.idocv.docview.service.ThdService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.util.UrlUtil;
import com.idocv.docview.vo.DocVo;

@Service
public class ClusterServiceImpl implements ClusterService {

	private static final Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);
	
	@Resource
	private DocDao docDao;

	@Resource
	private RcUtil rcUtil;

	@Resource
	private ConvertService convertService;

	private @Value("${cluster.switch}")
	boolean clusterSwitch;

	private @Value("${cluster.upload2dfs.mode}")
	int clusterUpload2dfsMode = 0;

	private @Value("${cluster.dfs.server.upload}")
	String clusterDfsServerUpload;

	private @Value("${cluster.dfs.server.download}")
	String clusterDfsServerDonwload;

	@Value("${upload.max.size}")
	private Long uploadMaxSize;

	@Value("${upload.max.msg}")
	private String uploadMaxMsg;

	@Resource
	private ThdService thdService;

	@Override
	public DocVo add(String fileName, byte[] data, String appid, String uid,
			String tid, String sid, String mode) throws DocServiceException {
		try {
			if (!DocServiceImpl.validateExpireDate()) {
				System.out.println("[ERROR] This machine is expired!");
				return null;
			}

			// 1. validate user params
			thdService.validateUser(uid, tid, sid);

			// 2. get fileName md5
			int size = data.length;
			String rid = RcUtil.genRid(appid, fileName, size);
			String md5FileName = thdService.getFileMd5(rcUtil.getPath(rid));

			// 3. set DocPo and save to database
			DocPo doc = new DocPo();
			String uuid = RcUtil.getUuidByRid(rid);
			String ext = RcUtil.getExt(rid);
			doc.setRid(rid);
			doc.setUuid(uuid);
			doc.setName(fileName);
			doc.setSize(size);
			doc.setCtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			doc.setStatus(1);
			Map<String, Object> metas = new HashMap<String, Object>();
			metas.put("appid", appid);
			metas.put("uid", uid);
			metas.put("tid", tid);
			metas.put("sid", sid);
			metas.put("mode", mode);
			doc.setMetas(metas);
			String url = "dfs:///" + appid + "/" + md5FileName + "." + ext;
			doc.setUrl(url);

			if (!rcUtil.isSupportUpload(ext)) {
				throw new DocServiceException("不支持上传" + ext + "文件，详情请联系管理员！");
			}

			// save file meta and file
			FileUtils.writeByteArrayToFile(new File(rcUtil.getPath(rid)), data);

			// save info
			docDao.add(appid, uid, rid, uuid, fileName, size, ext, 1, null, metas);
			docDao.updateUrl(uuid, url);

			// Asynchronously convert document
			convertService.convert(rid);

			// 4. upload to cluster
			upload2DFSInstantly(uuid);

			// 5. return
			return DocServiceImpl.convertPo2Vo(doc);
		} catch (Exception e) {
			logger.error("[CLUSTER] add file error: " + e.getMessage());
			throw new DocServiceException(e.getMessage(), e);
		}
	}

	@Override
	@Async
	public void upload2DFSInstantly(String uuid) {
		if (!clusterSwitch || 0 == clusterUpload2dfsMode || 2 == clusterUpload2dfsMode) {
			return;
		}
		uploadUuid2Remote(uuid);
		System.out.println("[CLUSTER] upload file " + uuid + " success...");
	}

	@Override
	@Scheduled(cron = "${cluster.upload2dfs.cron}")
	public void upload2DFSBatchTask() {
		if (!clusterSwitch || 0 == clusterUpload2dfsMode) {
			return;
		}
		try {
			// get all files need to upload to DFS
			List<DocPo> newFileList = docDao.listNewlyAddedFiles();
			long startTime = System.currentTimeMillis();
			// upload those files sequentially
			if (!CollectionUtils.isEmpty(newFileList)) {
				for (DocPo newFile : newFileList) {
					String uuid = newFile.getUuid();
					uploadUuid2Remote(uuid);
				}
			}

			// upload elapse & file count
			long endTime = System.currentTimeMillis();
			long elapse = (endTime - startTime) / 1000;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			logger.info("[CLUSTER] successfully synchronised newly added "
					+ newFileList.size() + " files within " + elapse
					+ "second(s) from " + df.format(new Date(startTime))
					+ " to " + df.format(new Date(startTime)));
		} catch (DBException e) {
			logger.error("[CLUSTER] Get newly added files error: " + e.getMessage());
		}
	}

	/**
	 * upload the file to DFS as following steps<br />
	 * 1. check cluster switch: false:return, true:continue<br />
	 * 2. check if the file is already uploaded<br />
	 * 3. if YES, return, if NO, upload it<br />
	 * 4. update the upload status
	 * 
	 * @param uuid
	 */
	public void uploadUuid2Remote(String uuid) {
		if (!clusterSwitch) {
			return;
		}
		try {
			DocVo vo = DocServiceImpl.convertPo2Vo(docDao.getByUuid(uuid, false));
			String rid = vo.getRid();
			String src = rcUtil.getPath(rid);
			String fileName = vo.getName();
			Map<String, Object> params = vo.getMetas();
			
			// if already upload to remote DFS server, return
			if (null != params.get("remote") && "1".equals(params.get("remote"))) {
				return;
			}

			byte[] bytes = FileUtils.readFileToByteArray(new File(src));
			upload2Remote(fileName, bytes, params);

			// update remote status
			docDao.updateField(uuid, "metas.remote", "1");
			logger.info("[CLUSTER] upload uuid(" + uuid + ") to DFS success!");
		} catch (Exception e) {
			logger.error("[CLUSTER] upload uuid(" + uuid + ") to remote error: " + e.getMessage());
		}
	}

	/**
	 * Real upload process. Used by uploadUuid2Remote
	 * 
	 * @param fileName
	 * @param bytes
	 * @param params
	 * @return
	 */
	private boolean upload2Remote(String fileName, byte[] bytes, Map<String, Object> params) {
		try {
			HttpClient client = new HttpClient();
			StringBuffer paramString = new StringBuffer();
			String url = clusterDfsServerUpload;
			if (!CollectionUtils.isEmpty(params)) {
				for (Entry<String, Object> entry : params.entrySet()) {
					if (paramString.length() > 0) {
						paramString.append("&");
					}
					paramString.append(entry.getKey() + "=" + entry.getValue());
				}
				url = url + "?" + paramString;
			}
			PostMethod filePost = new PostMethod(url);
			Part[] parts = { new FilePart("filename", new ByteArrayPartSource(fileName, bytes)) {

				@Override
				protected void sendDispositionHeader(OutputStream out) throws IOException {
					super.sendDispositionHeader(out);
					String filename = getSource().getFileName();
					if (filename != null) {
						out.write(EncodingUtil.getAsciiBytes(FILE_NAME));
						out.write(QUOTE_BYTES);
						out.write(EncodingUtil.getBytes(filename, "utf-8"));
						out.write(QUOTE_BYTES);
					}
				}

			} };
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
			int status = client.executeMethod(filePost);
			String result = filePost.getResponseBodyAsString();
			if (200 == status && "0".equalsIgnoreCase(new ObjectMapper().readTree(result).get("ret").toString())) {
				// System.out.println("Upload success, result: " + result);
				logger.info("upload to remote success: " + result + ", params=" + params);
				return true;
			}
			logger.error("[CLUSTER] upload to remote error with result: " + result + ", params=" + params);
		} catch (Exception e) {
			logger.error("[CLUSTER] upload to remote error: " + e.getMessage() + ", params=" + params);
		}
		return false;
	}

	@Override
	public DocVo addUrl(String appId, String fileMd5, String ext) throws DocServiceException {
		try {
			// check file type
			if (!rcUtil.isSupportView(ext)) {
				throw new DocServiceException("暂不支持" + ext + "文件预览！");
			}
			
			String urlSuffix = appId + "/" + fileMd5 + "." + ext;
			String dbUrl = "dfs:///" + urlSuffix;
			DocVo vo = DocServiceImpl.convertPo2Vo(docDao.getUrl(dbUrl, false));
			if (null != vo) {
				return vo;
			}
			
			String realUrl = clusterDfsServerDonwload + urlSuffix;
			String host = DocServiceImpl.getHost(realUrl);
			Response urlResponse = null;
			try {
				String encodedUrl = UrlUtil.encodeUrl(realUrl);
				encodedUrl = encodedUrl.replaceAll(" ", "%20");
				encodedUrl = encodedUrl.contains("://") ? encodedUrl : ("http://" + encodedUrl);
				// urlResponse = Jsoup.connect(url).referrer(host).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreContentType(true).execute();
				urlResponse = Jsoup.connect(encodedUrl).maxBodySize(uploadMaxSize.intValue() + 1000).referrer(host).timeout(60000).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreHttpErrors(true).followRedirects(true).ignoreContentType(true).execute();
				if (urlResponse.statusCode() == 307) {
					String sNewUrl = urlResponse.header("Location");
					if (sNewUrl != null && sNewUrl.length() > 7) {
						encodedUrl = sNewUrl;
					}
					urlResponse = Jsoup.connect(encodedUrl).referrer(host).timeout(5000).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreHttpErrors(true).followRedirects(true).ignoreContentType(true).execute();
				}
				if (null == urlResponse) {
					logger.error("[CLUSTER] return NULL when access " + realUrl);
					throw new Exception("获取资源(" + realUrl + ")时返回为空！");
				}
			} catch (Exception e) {
				logger.error("[CLUSTER] ERROR when access " + realUrl + " : " + e.getMessage());
				throw new DocServiceException("无法访问资源（" + realUrl + "）");
			}

			// save data to local
			byte[] data = urlResponse.bodyAsBytes();
			int size = data.length;
			String fileName = fileMd5 + "." + ext;
			String rid = RcUtil.genRid(appId, fileName, size);
			DocPo doc = new DocPo();
			String uuid = RcUtil.getUuidByRid(rid);
			doc.setRid(rid);
			doc.setUuid(uuid);
			doc.setName(fileMd5 + "." + ext);
			doc.setSize(size);
			doc.setCtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			doc.setStatus(1);
			String url = "dfs:///" + appId + "/" + fileName;
			doc.setUrl(url);
			if (!rcUtil.isSupportUpload(ext)) {
				throw new DocServiceException("不支持上传" + ext + "文件，详情请联系管理员！");
			}

			// save file meta and file
			FileUtils.writeByteArrayToFile(new File(rcUtil.getPath(rid)), data);

			// save info
			docDao.add(appId, null, rid, uuid, fileName, size, ext, 1, null, null);

			// Asynchronously convert document
			convertService.convert(rid);
			return DocServiceImpl.convertPo2Vo(doc);
		} catch (Exception e) {
			logger.error("[CLUSTER] add remote file(" + appId + "/" + fileMd5 + "." + ext + ") to local error: " + e.getMessage());
			throw new DocServiceException(e.getMessage());
		}
	}
}