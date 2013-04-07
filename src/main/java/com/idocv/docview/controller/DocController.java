package com.idocv.docview.controller;


import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.idocv.docview.common.DocResponse;
import com.idocv.docview.common.IpUtil;
import com.idocv.docview.common.Paging;
import com.idocv.docview.dao.DocDao.QueryOrder;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.AppService;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.PreviewService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;


@Controller
@RequestMapping("doc")
public class DocController {
	
	private static final Logger logger = LoggerFactory.getLogger(DocController.class);

	@Resource
	private AppService appService;
	
	@Resource
	private DocService docService;

	@Resource
	private PreviewService previewService;

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
	public String add(HttpServletRequest req,
			@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "token", defaultValue = "doctest") String token,
			@RequestParam(value = "mode", defaultValue = "public") String modeString) {
		try {
			int mode = 0;
			if ("public".equalsIgnoreCase(modeString)) {
				mode = 1;
			}
			String ip = IpUtil.getIpAddr(req);
			byte[] data = file.getBytes();
			String name = file.getOriginalFilename();
			DocVo vo = docService.add(token, name, data, mode);
			logger.info("--> " + ip + " ADD " + vo.getRid());
			System.err.println("--> " + ip + " ADD " + vo.getRid());
			return "{\"uuid\":\"" + vo.getUuid() + "\"}";
		} catch (Exception e) {
			logger.error("upload error <controller>: ", e);
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
			System.out.println("Result: " + result);
			return "true";
		} catch (Exception e) {
			logger.error("delete error <controller>: ", e);
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
			logger.error("delete error <controller>: ", e);
			return "{\"error\":" + e.getMessage() + "}";
		}
	}

	/**
	 * 页面列表
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("list")
	public Paging<DocVo> list(
			HttpServletRequest req,
			@RequestParam(value = "iDisplayStart", defaultValue = "0") Integer start,
			@RequestParam(value = "iDisplayLength", defaultValue = "10") Integer length,
			@RequestParam(value = "sSearch", required = false) String sSearch,
			@RequestParam(value = "iSortCol_0", defaultValue = "0") String sortIndex,
			@RequestParam(value = "sSortDir_0", defaultValue = "desc") String sortDirection) {
		try {
			String sortName = req.getParameter("mDataProp_" + sortIndex);
			QueryOrder queryOrder = QueryOrder.getQueryOrder(sortName, sortDirection);
			Paging<DocVo> list = docService.list(start, length, sSearch, queryOrder);
			return list;
		} catch (DocServiceException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 下载
	 */
	@Deprecated
	@RequestMapping("download")
	public void download(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "id") String id) {
		try {
			DocVo vo = docService.get(id);
			String rid = vo.getRid();
			String path = rcUtil.getPath(rid);
			DocResponse.setResponseHeaders(req, resp, vo.getName());
			IOUtils.write(FileUtils.readFileToByteArray(new File(path)), resp.getOutputStream());
		} catch (Exception e) {
			logger.error("download error: ", e);
		}
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
			logger.error("download error: ", e);
		}
	}
}
