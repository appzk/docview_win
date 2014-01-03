package com.idocv.docview.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.dao.DocDao;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.DocPo;
import com.idocv.docview.service.ClusterService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;

@Service
public class ClusterServiceImpl implements ClusterService {

	private static final Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);
	
	@Resource
	private DocDao docDao;

	@Resource
	private RcUtil rcUtil;

	private @Value("${cluster.dfs.server.upload}")
	String clusterDfsServerUpload;

	@Override
	public DocVo add(String fileName, byte[] data, String appid, String uid,
			String tid, String sid, String mode) throws DocServiceException {
		System.out.println("[CLUSTER] adding file...");
		return null;
	}

	@Override
	public DocVo upload(DocPo po) throws DocServiceException {
		System.out.println("[CLUSTER] uploading file...");
		return null;
	}

	@Override
	@Async
	public void upload2DFSInstantly(String uuid) {
		System.out.println("[CLUSTER] uploading file " + uuid + "...");
	}

	@Override
	@Scheduled(cron = "${cluster.upload2dfs.cron}")
	public void upload2DFSBatch() {
		DateFormat dateFormat = DateFormat.getDateTimeInstance();
		String formattedDate = dateFormat.format(new Date());
		System.out.println("[CLUSTER] current time: " + formattedDate);
	}

	public void uploadUuid2Remote(String uuid) {
		try {
			DocVo vo = DocServiceImpl.convertPo2Vo(docDao.getByUuid(uuid, false));
			String rid = vo.getRid();
			String src = rcUtil.getPath(rid);
			String fileName = vo.getName();
			Map<String, Object> params = vo.getMetas();
			byte[] bytes = FileUtils.readFileToByteArray(new File(src));
			upload2Remote(fileName, bytes, params);
		} catch (Exception e) {
			logger.error("[CLUSTER] upload uuid(" + uuid + ") to remote error: " + e.getMessage());
		}
	}

	public boolean upload2Remote(String fileName, byte[] bytes,
			Map<String, Object> params) {
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
				// TODO
				// remove from NEW file collection
				return true;
			}
			logger.error("[CLUSTER] upload to remote error with result: " + result + ", params=" + params);
		} catch (Exception e) {
			logger.error("[CLUSTER] upload to remote error: " + e.getMessage() + ", params=" + params);
		}
		return false;
	}
}