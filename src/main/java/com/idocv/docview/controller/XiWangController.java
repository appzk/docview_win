package com.idocv.docview.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.ClusterService;
import com.idocv.docview.vo.DocVo;

@Controller
public class XiWangController {

	private static final Logger logger = LoggerFactory.getLogger(XiWangController.class);
	
	@Resource
	private ClusterService clusterService;

	private @Value("${view.page.load.async}")
	boolean pageLoadAsync;

	/**
	 * upload
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("xiwang/upload")
	public Map<String, Object> upload(@RequestParam MultipartFile file,
			@RequestParam(value = "appid") String appid,
			@RequestParam(value = "uid") String uid,
			@RequestParam(value = "tid") String tid,
			@RequestParam(value = "sid") String sid,
			@RequestParam(value = "mode") String mode) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			byte[] data = file.getBytes();
			String fileName = file.getOriginalFilename();
			DocVo vo = clusterService.add(fileName, data, appid, uid, tid, sid, mode);
			if (null == vo) {
				throw new Exception("上传失败！");
			}
			logger.info("[CLUSTER] USER( " + uid + ") uploaded file: " + vo.getUuid());
			result.put("uuid", vo.getUuid());
			String dfsUrl = vo.getUrl();
			if (StringUtils.isNotBlank(dfsUrl) && dfsUrl.matches("dfs:/{2,3}[^/]+/(\\w{32}\\.\\w+)")) {
				result.put("md5filename", dfsUrl.replaceFirst("dfs:/{2,3}[^/]+/(\\w{32}\\.\\w+)", "$1"));
			}
			
			result.putAll(vo.getMetas());
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
	@RequestMapping("{appId}/{fileMd5:\\w{32}}.{ext:[a-zA-Z]{3,4}}")
	public String view(Model model,
			@PathVariable(value = "appId") String appId,
			@PathVariable(value = "fileMd5") String fileMd5,
			@PathVariable(value = "ext") String ext) {
		try {
			DocVo vo = clusterService.addUrl(appId, fileMd5, ext);
			if (null == vo) {
				throw new DocServiceException("获取文件失败！");
			}
			String uuid = vo.getUuid();
			return "redirect:/view/" + uuid + (pageLoadAsync ? "" : ".html");
		} catch (Exception e) {
			logger.error("[CLUSTER] view " + appId + "/" + fileMd5 + "." + ext + " error: " + e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "404";
		}
	}
}