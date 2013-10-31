package com.idocv.docview.controller;


import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.service.LabelService;
import com.idocv.docview.vo.LabelVo;


@Controller
@RequestMapping("label")
public class LabelController {
	
	private static final Logger logger = LoggerFactory.getLogger(LabelController.class);

	@Resource
	private LabelService labelService;

	@ResponseBody
	@RequestMapping("{uid}.json")
	public List<LabelVo> list(@PathVariable(value = "uid") String uid) {
		try {
			List<LabelVo> list = labelService.list(uid);
			return list;
		} catch (Exception e) {
			logger.error("label uid.json error: " + e.getMessage());
			return null;
		}
	}

}