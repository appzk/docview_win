package com.idocv.docview.controller;

import java.io.File;
import java.io.Serializable;
import java.net.URLDecoder;
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
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.idocv.docview.common.DocResponse;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.ClusterService;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;
import com.idocv.docview.vo.OfficeBaseVo;
import com.idocv.docview.vo.PageVo;

@Controller
public class XiWangController {

	private static final Logger logger = LoggerFactory.getLogger(XiWangController.class);
	
	@Resource
	private ClusterService clusterService;

	private @Value("${view.page.load.async}")
	boolean pageLoadAsync;

	private @Value("${thd.view.check.keys}")
	String thdViewCheckKeys;

	@Resource
	private RcUtil rcUtil;
	
	@Resource
	private DocService docService;

	@Resource
	private ViewService viewService;

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
			if (StringUtils.isBlank(thdViewCheckKeys) || !thdViewCheckKeys.matches(".*?" + appId + "@(\\w+):(\\d).*")) {
				logger.error("对不起，不存在该应用(" + appId + ")！");
				model.addAttribute("error", "对不起，不存在该应用！");
				return "404";
			}
			String appKey = thdViewCheckKeys.replaceFirst(".*?" + appId + "@(\\w+):(\\d).*", "$1");
			String appSwitch = thdViewCheckKeys.replaceFirst(".*?" + appId + "@(\\w+):(\\d).*", "$2");
			if ("1".equals(appSwitch)) {
				// check user
				if (StringUtils.isBlank(key) || StringUtils.isBlank(userId) || StringUtils.isBlank(salt)) {
					logger.error("该文档(" + appId + "/" + fileMd5
							+ ")需要用户验证，请提供必要参数(key=" + key + ", userId="
							+ userId + ", salt=" + salt + ")！");
					model.addAttribute("error", "该文档需要用户验证，请提供必要参数！");
					return "404";
				}
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

	@RequestMapping("/view/url/cw")
	public String viewUrl(HttpServletRequest req, Model model,
			@RequestParam(required = true) String url,
			@RequestParam(value = "appId") String appId,
			@RequestParam(value = "fname", required = false) String name,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "salt", required = false) String salt) {
		try {
			if (StringUtils.isBlank(thdViewCheckKeys)
					|| !thdViewCheckKeys.matches(".*?" + appId
							+ "@(\\w+):(\\d).*")) {
				logger.error("对不起，不存在该应用(" + appId + ")！");
				model.addAttribute("error", "对不起，不存在该应用！");
				return "404";
			}
			String appKey = thdViewCheckKeys.replaceFirst(".*?" + appId
					+ "@(\\w+):(\\d).*", "$1");
			String appSwitch = thdViewCheckKeys.replaceFirst(".*?" + appId
					+ "@(\\w+):(\\d).*", "$2");
			if ("1".equals(appSwitch)) {
				// check user
				if (StringUtils.isBlank(key) || StringUtils.isBlank(userId)
						|| StringUtils.isBlank(salt)) {
					logger.error("该URL文档(" + appId + "/" + url
							+ ")需要用户验证，请提供必要参数(key=" + key + ", userId="
							+ userId + ", salt=" + salt + ")！");
					model.addAttribute("error", "该文档需要用户验证，请提供必要参数！");
					return "404";
				}
				String rawKey = userId + salt + appKey;
				String retKey = DigestUtils.md5Hex(rawKey);
				if (!key.equalsIgnoreCase(retKey)) {
					logger.error("验证失败，您无权查看该URL文件(" + appId + "/" + url + "！");
					model.addAttribute("error", "验证失败，您无权查看该文件！");
					return "404";
				}
			}
			url = URLDecoder.decode(url, "UTF-8");
			String queryString = req.getQueryString();
			DocVo vo = docService.addUrl(appId, userId, name, url, 1, null);
			if (null == vo) {
				throw new DocServiceException("获取文件失败！");
			}
			String uuid = vo.getUuid();
			return "redirect:/view/"
					+ uuid
					+ (pageLoadAsync ? "" : ".html")
					+ (StringUtils.isBlank(queryString) ? "" : "?"
							+ queryString);
		} catch (Exception e) {
			logger.error("view url(" + url + ") error: " + e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "404";
		}
	}

	/**
	 * get document content by uuid in json format
	 * 
	 * 1. get docVo by uuid 2. check access mode of docVo 3. public mode ->
	 * direct view 4. semi-public | private mode -> 5 5. get sessionVo by
	 * sessionId 6. current time - ctime > expire time ? session expired : view.
	 * PageVo<? extends Serializable> page = null;
	 * 
	 */
	@RequestMapping("{appId}/{fileMd5:\\w{32}}.{ext:[\\w]{3,4}}.json")
	@ResponseBody
	public PageVo<? extends Serializable> jsonUuid(HttpServletRequest req,
			HttpServletResponse resp, Model model,
			@PathVariable(value = "appId") String appId,
			@PathVariable(value = "fileMd5") String fileMd5,
			@PathVariable(value = "ext") String ext,
			@RequestParam(value = "fname", required = false) String name,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "salt", required = false) String salt,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size) {
		PageVo<? extends Serializable> page = null;
		String rid = null;
		String uuid = null;
		try {
			DocVo vo = clusterService.addUrl(appId, fileMd5, ext);
			if (null == vo) {
				throw new DocServiceException("获取文件失败！");
			}
			uuid = vo.getUuid();
			// 1. get docVo by uuid
			DocVo docVo = docService.getByUuid(uuid);
			if (null == docVo || StringUtils.isBlank(docVo.getRid())) {
				throw new DocServiceException("文档(" + uuid + ")不存在！");
			}
			rid = docVo.getRid();
			ext = RcUtil.getExt(rid);

			// 2. check access mode of docVo
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)
					|| "odt".equalsIgnoreCase(ext)) {
				start = (start - 1) * size + 1;
				page = viewService.convertWord2Html(rid, start, size);
			} else if ("xls".equalsIgnoreCase(ext)
					|| "xlsx".equalsIgnoreCase(ext)
					|| "ods".equalsIgnoreCase(ext)) {
				page = viewService.convertExcel2Html(rid, start, size);
			} else if ("ppt".equalsIgnoreCase(ext)
					|| "pptx".equalsIgnoreCase(ext)
					|| "odp".equalsIgnoreCase(ext)) {
				page = viewService.convertPPT2Img(rid, start, size);
			} else if ("txt".equalsIgnoreCase(ext)) {
				start = (start - 1) * size + 1;
				page = viewService.convertTxt2Html(rid, start, size);
			} else if ("pdf".equalsIgnoreCase(ext)) {
				// page = previewService.convertPdf2Html(rid, 1, 0);
				page = viewService.convertPdf2Img(rid, 1, 0);
			} else {
				page = new PageVo<OfficeBaseVo>(null, 0);
				page.setCode(0);
				page.setDesc("不是一个文档！");
			}
			if (CollectionUtils.isEmpty(page.getData())) {
				page.setCode(0);
				page.setDesc("没有可显示的内容！");
			}
			page.setName(docVo.getName());
			page.setRid(docVo.getRid());
			page.setUuid(docVo.getUuid());
			docService.logView(uuid);
		} catch (Exception e) {
			logger.error("view json(" + uuid + ") error: " + e.getMessage());
			page = new PageVo<OfficeBaseVo>(null, 0);
			page.setCode(0);
			page.setDesc(e.getMessage());
			page.setUuid(uuid);
			page.setRid(rid);
		}
		return page;
	}

	/**
	 * get document content by uuid in json format
	 * 
	 * 1. get docVo by uuid 2. check access mode of docVo 3. public mode ->
	 * direct view 4. semi-public | private mode -> 5 5. get sessionVo by
	 * sessionId 6. current time - ctime > expire time ? session expired : view.
	 * PageVo<? extends Serializable> page = null;
	 * 
	 */
	@RequestMapping("{appId}/{fileMd5:\\w{32}}.{ext:[\\w]{3,4}}.jsonp")
	public void jsonpUuid(HttpServletRequest req,
			HttpServletResponse resp, Model model,
			@PathVariable(value = "appId") String appId,
			@PathVariable(value = "fileMd5") String fileMd5,
			@PathVariable(value = "ext") String ext,
			@RequestParam(value = "fname", required = false) String name,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "salt", required = false) String salt,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(required = true) String callback) {
		PageVo<? extends Serializable> page = null;
		String rid = null;
		String uuid = null;
		try {
			DocVo vo = clusterService.addUrl(appId, fileMd5, ext);
			if (null == vo) {
				throw new DocServiceException("获取文件失败！");
			}
			uuid = vo.getUuid();
			// 1. get docVo by uuid
			DocVo docVo = docService.getByUuid(uuid);
			if (null == docVo || StringUtils.isBlank(docVo.getRid())) {
				throw new DocServiceException("文档(" + uuid + ")不存在！");
			}
			rid = docVo.getRid();
			ext = RcUtil.getExt(rid);

			// 2. check access mode of docVo
			if ("doc".equalsIgnoreCase(ext) || "docx".equalsIgnoreCase(ext)
					|| "odt".equalsIgnoreCase(ext)) {
				start = (start - 1) * size + 1;
				page = viewService.convertWord2Html(rid, start, size);
			} else if ("xls".equalsIgnoreCase(ext)
					|| "xlsx".equalsIgnoreCase(ext)
					|| "ods".equalsIgnoreCase(ext)) {
				page = viewService.convertExcel2Html(rid, start, size);
			} else if ("ppt".equalsIgnoreCase(ext)
					|| "pptx".equalsIgnoreCase(ext)
					|| "odp".equalsIgnoreCase(ext)) {
				page = viewService.convertPPT2Img(rid, start, size);
			} else if ("txt".equalsIgnoreCase(ext)) {
				start = (start - 1) * size + 1;
				page = viewService.convertTxt2Html(rid, start, size);
			} else if ("pdf".equalsIgnoreCase(ext)) {
				// page = previewService.convertPdf2Html(rid, 1, 0);
				page = viewService.convertPdf2Img(rid, 1, 0);
			} else {
				page = new PageVo<OfficeBaseVo>(null, 0);
				page.setCode(0);
				page.setDesc("不是一个文档！");
			}
			if (CollectionUtils.isEmpty(page.getData())) {
				page.setCode(0);
				page.setDesc("没有可显示的内容！");
			}
			page.setName(docVo.getName());
			page.setRid(docVo.getRid());
			page.setUuid(docVo.getUuid());
			docService.logView(uuid);
		} catch (Exception e) {
			logger.error("view json(" + uuid + ") error: " + e.getMessage());
			page = new PageVo<OfficeBaseVo>(null, 0);
			page.setCode(0);
			page.setDesc(e.getMessage());
			page.setUuid(uuid);
			page.setRid(rid);
		}
		try {
			String result = callback + "(" + new ObjectMapper().writeValueAsString(page) + ");";
			resp.getWriter().write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}