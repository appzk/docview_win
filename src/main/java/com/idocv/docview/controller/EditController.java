package com.idocv.docview.controller;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.common.ViewType;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.EditService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.ViewBaseVo;


@Controller
@RequestMapping("edit")
public class EditController {
	
	private static final Logger logger = LoggerFactory.getLogger(EditController.class);

	@Resource
	private DocService docService;

	@Resource
	private ViewService viewService;
	
	@Resource
	private EditService editService;

	// TODO
	// Etherpad ref: https://github.com/ether/etherpad-lite

	/**
	 * 保存版本
	 * 
	 * @param uuid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("{uuid}/save")
	public Map<String, String> save(@PathVariable(value = "uuid") String uuid,
			@RequestParam(value = "body") String body) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			// Check doc type
			if (!uuid.endsWith(ViewType.WORD.getSymbol())) {
				throw new Exception("暂时只支持word文档协作编辑！");
			}

			// Check user
			// TODO

			// TODO
			// save the content.
			editService.save(uuid, body);

			result.put("code", "1");
			result.put("msg", "success");
		} catch (Exception e) {
			logger.error("Load editor error: ", e);
			result.put("code", "0");
			result.put("msg", e.getMessage());
		}
		return result;
	}

	/**
	 * 加载编辑页面
	 * 
	 * @param uuid
	 * @return
	 */
	@RequestMapping("{uuid}")
	public String load(@PathVariable(value = "uuid") String uuid) {
		try {
			// Check doc type
			if (!uuid.endsWith(ViewType.WORD.getSymbol())) {
				throw new Exception("暂时只支持word文档协作编辑！");
			}

			// Check user
			// TODO

			return "word/edit";
		} catch (Exception e) {
			logger.error("Load editor error: ", e);
			return "{\"error\":" + e.getMessage() + "}";
		}
	}

	/**
	 * 加载数据
	 * 
	 * @param uuid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("{uuid}.json")
	public PageVo<? extends Serializable> loadJson(@PathVariable(value = "uuid") String uuid) {
		PageVo<? extends Serializable> page = null;
		String rid = null;
		try {
			DocVo docVo = docService.getByUuid(uuid);
			if (null == docVo || StringUtils.isBlank(docVo.getRid())) {
				throw new DocServiceException("文档(" + uuid + ")不存在！");
			}
			rid = docVo.getRid();
			String ext = RcUtil.getExt(rid);
			int accessMode = docVo.getStatus();
			if (ViewType.WORD == ViewType.getViewType(ext)) {
				page = viewService.convertWord2HtmlAll(rid);
			} else {
				page = new PageVo<ViewBaseVo>(null, 0);
				page.setCode(0);
				page.setDesc("该文件不支持在线编辑！");
			}
			if (CollectionUtils.isEmpty(page.getData())) {
				page.setCode(0);
				page.setDesc("没有可显示的内容！");
			}
			page.setName(docVo.getName());
			page.setRid(docVo.getRid());
			page.setUuid(docVo.getUuid());
			page.setMd5(docVo.getMd5());
			docService.logView(uuid);
		} catch (Exception e) {
			logger.error("view id.json(" + uuid + ") error: " + e.getMessage());
			page = new PageVo<ViewBaseVo>(null, 0);
			page.setCode(0);
			page.setDesc(e.getMessage());
			page.setUuid(uuid);
			page.setRid(rid);
		}
		return page;
	}
}