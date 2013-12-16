package com.idocv.docview.controller;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.idocv.docview.common.DocResponse;
import com.idocv.docview.common.Paging;
import com.idocv.docview.dao.DocDao.QueryOrder;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.AppService;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.UserService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.IpUtil;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.AppVo;
import com.idocv.docview.vo.DocVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.PdfVo;
import com.idocv.docview.vo.UserVo;


@Controller
@RequestMapping("doc")
public class DocController {
	
	private static final Logger logger = LoggerFactory.getLogger(DocController.class);

	@Resource
	private AppService appService;
	
	@Resource
	private UserService userService;

	@Resource
	private DocService docService;

	@Resource
	private ViewService viewService;

	@Resource
	private RcUtil rcUtil;

	/**
	 * 上传
	 * 
	 * @param sid
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("upload")
	public String upload(HttpServletRequest req,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "url", required = false) String url,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "token", required = false) String token,
			@RequestParam(value = "mode", defaultValue = "public") String modeString,
			@RequestParam(value = "label", defaultValue = "") String label) {
		try {
			// Two ways to upload: App token upload & user Sid upload
			String ip = IpUtil.getIpAddr(req);

			// get access mode, 0-private, 1-public
			int mode = 1;
			if ("private".equalsIgnoreCase(modeString)) {
				mode = 0;
			}

			// get app & uid
			String app = null;
			String uid = null;
			if (StringUtils.isNotBlank(token)) {
				// 1. get app by token
				AppVo appPo = appService.getByToken(token);
				if (null == appPo || StringUtils.isBlank(appPo.getId())) {
					logger.error("不存在该应用，token=" + token);
					throw new DocServiceException(0, "不存在该应用！");
				}
				app = appPo.getId();
			} else {
				String sid = getSidByHttpServletRequest(req);
				if (StringUtils.isBlank(sid)) {
					logger.error("请先登录，或通过token来访问！");
					throw new DocServiceException("请先登录，或通过token来访问！");
				}
				UserVo userVo = userService.getBySid(sid);
				if (null == userVo) {
					logger.error("用户不存在或未登录！");
					throw new DocServiceException("用户不存在或未登录！");
				}
				int userStatus = userVo.getStatus();
				if (userStatus < 1) {
					logger.error("用户邮箱未验证！");
					throw new DocServiceException("用户邮箱未验证！");
				}
				if (userStatus == 1 && mode == 0) {
					logger.error("普通用户只能上传公开文档，要上传私有文档，请升级为会员！");
					throw new DocServiceException(
							"普通用户只能上传公开文档，要上传私有文档，请升级为会员！");
				}
				app = userVo.getApp();
				uid = userVo.getId();
			}

			// upload data
			DocVo vo = null;
			if (null != file) {
				// upload by multipart/form-data
				byte[] data = file.getBytes();
				if (StringUtils.isBlank(name)) {
					name = file.getOriginalFilename();
				}
				vo = docService.add(app, uid, name, data, mode, label);
			} else if (StringUtils.isNotBlank(url)) {
				// upload by URL
				vo = docService.addUrl(app, uid, name, url, mode, label);
			} else {
				throw new DocServiceException(0, "上传错误，file和url参数必选其一！");
			}

			if (null == vo) {
				throw new Exception("上传失败！");
			}
			logger.info("--> " + ip + " at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " ADD " + vo.getUuid());
			return "{\"uuid\":\"" + vo.getUuid() + "\"}";
		} catch (Exception e) {
			logger.error("upload error <controller>: " + e.getMessage());
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}
	
	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delete/{uuid}")
	public String delete(@PathVariable(value = "uuid") String uuid) {
		try {
			boolean result = docService.delete(uuid);
			return "true";
		} catch (Exception e) {
			logger.error("delete error <controller>: " + e.getMessage());
			return "{\"error\":" + e.getMessage() + "}";
		}
	}

	/**
	 * Set document access mode
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("mode/{uuid}/{mode}")
	public String mode(@PathVariable(value = "uuid") String uuid,
			@PathVariable(value = "mode") String modeString,
			@RequestParam(value = "token") String token) {
		try {
			int mode = 0;
			if ("public".equalsIgnoreCase(modeString)) {
				mode = 1;
			} else if ("private".equalsIgnoreCase(modeString)) {
				mode = 0;
			} else {
				throw new DocServiceException("NOT a valid mode!");
			}
			DocVo docVo = docService.getByUuid(uuid);
			if (null == docVo) {
				throw new DocServiceException("Document NOT found!");
			}
			docService.updateMode(token, uuid, mode);
			return "true";
		} catch (Exception e) {
			logger.error("delete error <controller>: " + e.getMessage());
			return "{\"error\":" + e.getMessage() + "}";
		}
	}

	/**
	 * 文档列表 1. sid存在（已登录） a. 普通用户：列出对应用户文档（包括私有文档） b. 应用管理员用户：列出对应应用文档（包括私有文档）
	 * 2. sid不存在（未登录）等有app名称 列出对应app公开文档
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("list.json")
	public Paging<DocVo> list(
			HttpServletRequest req,
			@RequestParam(value = "app", required = false) String app,
			@RequestParam(value = "iDisplayStart", defaultValue = "0") Integer start,
			@RequestParam(value = "iDisplayLength", defaultValue = "10") Integer length,
			@RequestParam(value = "sSearch", required = false) String sSearch,
			@RequestParam(value = "iSortCol_0", defaultValue = "0") String sortIndex,
			@RequestParam(value = "sSortDir_0", defaultValue = "desc") String sortDirection,
			@RequestParam(value = "label", required = false) String label) {
		try {
			String sid = getSidByHttpServletRequest(req);
			String sortName = req.getParameter("mDataProp_" + sortIndex);
			QueryOrder queryOrder = QueryOrder.getQueryOrder(sortName, sortDirection);
			Paging<DocVo> list = docService.list(app, sid, start, length, label, sSearch, queryOrder);
			return list;
		} catch (DocServiceException e) {
			logger.error("doc list error: " + e.getMessage());
			return new Paging<DocVo>(new ArrayList<DocVo>(), 0);
		}
	}

	/**
	 * Load the page of label document list page.
	 * 
	 * @return
	 */
	@RequestMapping("list/{label}")
	public String listLabel(
			HttpServletRequest req,
			@PathVariable(value = "label") String label) {
		return "doc/list-user";
	}

	/**
	 * 下载
	 */
	@RequestMapping("download/{uuid}")
	public void downloadByUuid(HttpServletRequest req,
			HttpServletResponse resp, @PathVariable(value = "uuid") String uuid) {
		try {
			DocVo vo = docService.getByUuid(uuid);
			String rid = vo.getRid();
			String path = rcUtil.getPath(rid);
			DocResponse.setResponseHeaders(req, resp, vo.getName());
			IOUtils.write(FileUtils.readFileToByteArray(new File(path)), resp.getOutputStream());
			docService.logDownload(uuid);
		} catch (Exception e) {
			logger.error("doc download error: " + e.getMessage());
		}
	}
	
	/**
	 * 下载PDF
	 */
	@RequestMapping("{uuid}/pdf")
	public void downloadPdfByUuid(HttpServletRequest req,
			HttpServletResponse resp,
			@PathVariable(value = "uuid") String uuid,
			@RequestParam(value = "stamp", required = false) String stamp,
			@RequestParam(value = "x", defaultValue = "0") float x,
			@RequestParam(value = "y", defaultValue = "0") float y) {
		try {
			DocVo vo = docService.getByUuid(uuid);
			String rid = vo.getRid();
			PageVo<PdfVo> page = viewService.convertWord2PdfStamp(rid, stamp, x, y);
			if (null == page || CollectionUtils.isEmpty(page.getData())) {
				return;
			}
			PdfVo pdfVo = page.getData().get(0);
			File destFile = pdfVo.getDestFile();
			String nameRaw = vo.getName();
			String name = nameRaw.substring(0, nameRaw.lastIndexOf("."));
			name = name + destFile.getName().substring(5);
			DocResponse.setResponseHeaders(req, resp, name);
			IOUtils.write(FileUtils.readFileToByteArray(destFile), resp.getOutputStream());
			docService.logDownload(uuid);
		} catch (Exception e) {
			logger.error("doc download error: " + e.getMessage());
		}
	}

	private static String getSidByHttpServletRequest(HttpServletRequest req) {
		String sid = null;
		if (null == req) {
			return null;
		}
		Cookie[] cookies = req.getCookies();
		if (null == cookies || cookies.length < 1) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if ("IDOCVSID".equalsIgnoreCase(cookie.getName())) {
				sid = cookie.getValue();
				break;
			}
		}
		return sid;
	}
}