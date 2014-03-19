package com.idocv.docview.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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

import com.idocv.docview.common.DocResponse;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.ClusterService;
import com.idocv.docview.service.DocService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;

@Controller
public class XiWangController {

	private static final Logger logger = LoggerFactory.getLogger(XiWangController.class);
	
	@Resource
	private ClusterService clusterService;

	private @Value("${view.page.load.async}")
	boolean pageLoadAsync;
	
	private @Value("${thd.view.check.switch}")
	boolean thdViewCheckSwitch;

	private @Value("${thd.view.check.keys}")
	String thdViewCheckKeys;

	@Resource
	private RcUtil rcUtil;
	
	@Resource
	private DocService docService;

	/**
	 * upload
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = { "xiwang/upload", "ciwong/upload" })
	public Map<String, Object> upload(@RequestParam MultipartFile file,
			@RequestParam(value = "appid") String appid,
			@RequestParam(value = "uid") String uid,
			@RequestParam(value = "tid") String tid,
			@RequestParam(value = "sid") String sid,
			@RequestParam(value = "mode") int mode) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			byte[] data = file.getBytes();
			String fileName = file.getOriginalFilename();
			long start = System.currentTimeMillis();
			DocVo vo = clusterService.add(fileName, data, appid, uid, tid, sid, mode);
			long end = System.currentTimeMillis();
			if (null == vo) {
				throw new Exception("上传失败！");
			}
			logger.info("[CLUSTER] USER( " + uid + ") uploaded file: " + vo.getUuid() + ", length: " + data.length + ", elapse: " + (end - start) + " miliseconds.");
			dataMap.put("uuid", vo.getUuid());
			String dfsUrl = vo.getUrl();
			if (StringUtils.isNotBlank(dfsUrl) && dfsUrl.matches("dfs:/{2,3}[^/]+/(\\w{32}\\.\\w+)")) {
				dataMap.put("md5filename", dfsUrl.replaceFirst("dfs:/{2,3}[^/]+/(\\w{32}\\.\\w+)", "$1"));
			}
			dataMap.putAll(vo.getMetas());
			dataMap.put("filename", fileName);
			String ext = (StringUtils.isNotBlank(fileName) && fileName
					.contains(".")) ? fileName.substring(fileName
					.lastIndexOf(".") + 1) : "";
			dataMap.put("suffix", ext);
			dataMap.put("filesize", data.length);
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			dataList.add(dataMap);
			result.put("data", dataList);
			result.put("ret", 0);
			result.put("errcode", 0);
			result.put("msg", "success");
			return result;
		} catch (Exception e) {
			logger.error("upload error <controller>: " + e.getMessage());
			result.put("ret", -1);
			result.put("msg", e.getMessage());
			return result;
		}
	}

	/**
	 * Cluster View
	 * 
	 * @param appId
	 * @param md5Filename
	 * @param ext
	 * @return
	 */
	@RequestMapping("{appId}/{fileMd5:\\w{32}}.{ext:[\\w]{3,4}}")
	public String view(
			HttpServletRequest req,
			HttpServletResponse resp,
			Model model,
			@PathVariable(value = "appId") String appId,
			@PathVariable(value = "fileMd5") String fileMd5,
			@PathVariable(value = "ext") String ext,
			@RequestParam(value = "fname", required = false) String name,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "salt", required = false) String salt) {
		try {
			if (thdViewCheckSwitch) {
				// check user
				if (StringUtils.isBlank(key) || StringUtils.isBlank(userId) || StringUtils.isBlank(salt)) {
					logger.error("该文档(" + appId + "/" + fileMd5
							+ ")需要用户验证，请提供必要参数(key=" + key + ", userId="
							+ userId + ", salt=" + salt + ")！");
					model.addAttribute("error", "该文档需要用户验证，请提供必要参数！");
					return "404";
				}
				if (StringUtils.isBlank(thdViewCheckKeys) || !thdViewCheckKeys.contains(appId)) {
					logger.error("对不起，不存在该应用(" + appId + ")！");
					model.addAttribute("error", "对不起，不存在该应用！");
					return "404";
				}
				String appKey = thdViewCheckKeys.replaceFirst(".*?" + appId + "@(\\w+).*", "$1");
				String rawKey = userId + salt + appKey;
				String retKey = DigestUtils.md5Hex(rawKey);
				if (!key.equalsIgnoreCase(retKey)) {
					logger.error("验证失败，您无权查看该文件(" + appId + "/" + fileMd5 + "！");
					model.addAttribute("error", "验证失败，您无权查看该文件！");
					return "404";
				}
			}

			String queryString = req.getQueryString();
			DocVo vo = clusterService.addUrl(appId, fileMd5, ext);
			if (null == vo) {
				throw new DocServiceException("获取文件失败！");
			}
			String uuid = vo.getUuid();
			if (StringUtils.isNotBlank(name)) {
				String rid = vo.getRid();
				String path = rcUtil.getPath(rid);
				DocResponse.setResponseHeaders(req, resp, name);
				IOUtils.write(FileUtils.readFileToByteArray(new File(path)), resp.getOutputStream());
				docService.logDownload(uuid);
				return null;
			}
			return "redirect:/view/" + uuid + (pageLoadAsync ? "" : ".html") + (StringUtils.isBlank(queryString) ? "" : "?" + queryString);
		} catch (Exception e) {
			logger.error("[CLUSTER] view " + appId + "/" + fileMd5 + "." + ext + " error: " + e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "404";
		}
	}
}