package com.idocv.docview.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.idocv.docview.common.DocResponse;
import com.idocv.docview.service.AppService;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.UserService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;


@Controller
@RequestMapping("draw")
public class DrawController {
	
	private static final Logger logger = LoggerFactory.getLogger(DrawController.class);

	private static ObjectMapper om = new ObjectMapper();

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

	@ResponseBody
	@RequestMapping("save/{uuid}")
	public Map<String, Object> save(@PathVariable(value = "uuid") String uuid,
									@RequestParam(value = "uid", required = true) String uid,
									@RequestParam(value = "data", required = true) String data) {
		try {
			DocVo docVo = docService.getByUuid(uuid);
			String docParseDir = rcUtil.getParseDir(docVo.getRid());
			File drawFile = new File(docParseDir + "draw", uid + ".json");
			FileUtils.writeStringToFile(drawFile, data, "UTF-8", false);
			return DocResponse.getSuccessResponseMap();
		} catch (Exception e) {
			logger.error("save draw error <controller>: " + e.getMessage());
			return DocResponse.getErrorResponseMap(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping("get/{uuid}")
	public Map<String, Object> save(@PathVariable(value = "uuid") String uuid,
									@RequestParam(value = "uid", required = false) String uid) {
		try {
			DocVo docVo = docService.getByUuid(uuid);
			String docParseDir = rcUtil.getParseDir(docVo.getRid());
			File drawFile = new File(docParseDir + "draw", uid + ".json");
			String linesStr = FileUtils.readFileToString(drawFile, "UTF-8");
			Map<String, Object> responseMap = DocResponse.getSuccessResponseMap();
			responseMap.put("data", linesStr);
			return responseMap;
		} catch (Exception e) {
			logger.error("save draw error <controller>: " + e.getMessage());
			return DocResponse.getErrorResponseMap(e.getMessage());
		}
	}
}