package com.idocv.docview.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.idocv.docview.service.ClusterService;
import com.idocv.docview.vo.DocVo;

@Controller
public class XiWangController {

	private static final Logger logger = LoggerFactory.getLogger(XiWangController.class);
	
	@Resource
	private ClusterService clusterService;

	/**
	 * upload
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("xiwang/upload")
	public Map<String, String> upload(@RequestParam MultipartFile file,
			@RequestParam(value = "appid") String appid,
			@RequestParam(value = "uid") String uid,
			@RequestParam(value = "tid") String tid,
			@RequestParam(value = "sid") String sid,
			@RequestParam(value = "mode") String mode) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			byte[] data = file.getBytes();
			String fileName = file.getOriginalFilename();
			DocVo vo = clusterService.add(fileName, data, appid, uid, tid, sid, mode);
			if (null == vo) {
				throw new Exception("上传失败！");
			}
			logger.info("[CLUSTER] USER( " + uid + ") uploaded file: " + vo);
			result.put("uuid", vo.getUuid());
		} catch (Exception e) {
			logger.error("upload error <controller>: " + e.getMessage());
			result.put("error", e.getMessage());
		}
		return result;
	}

	/**
	 * Cluster View
	 * 
	 * @param appId
	 * @param md5Filename
	 * @param ext
	 * @return
	 */
	@ResponseBody
	@RequestMapping("{appId}/{md5Filename:\\w{32}}.{ext:[a-zA-Z]{3,4}}")
	public Map<String, String> test(
			@PathVariable(value = "appId") String appId,
			@PathVariable(value = "md5Filename") String md5Filename,
			@PathVariable(value = "ext") String ext) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("code", "1");
		result.put("app", appId);
		result.put("md5", md5Filename);
		result.put("ext", ext);
		return result;
	}

}