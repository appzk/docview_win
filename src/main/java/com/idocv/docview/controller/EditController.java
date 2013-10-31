package com.idocv.docview.controller;


import javax.annotation.Resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.etherpad_lite_client.EPLiteClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.idocv.docview.service.EditService;
import com.idocv.docview.service.ViewService;


@Controller
@RequestMapping("edit")
public class EditController {
	
	private static final Logger logger = LoggerFactory.getLogger(EditController.class);

	@Resource
	private ViewService previewService;
	
	@Resource
	private EditService editService;

	/**
	 * 加载Etherpad编辑器
	 * ref: https://github.com/ether/etherpad-lite
	 * 
	 * @param uuid
	 * @return
	 */
	@RequestMapping("{uuid}")
	public String loadEditor(@PathVariable(value = "uuid") String uuid) {
		try {
			// Check doc type
			if (!uuid.endsWith("w")) {
				throw new Exception("暂时只支持word文档协作编辑！");
			}

			// Check user
			// TODO

			// Load editor
			String htmlBody = editService.getHtmlBody(uuid);
			EPLiteClient client = new EPLiteClient("http://edit.idocv.com", "BVuGNrqJxvBZOS4F3VQ2WQXNT2ntRiTy");
			try {
				// Create pad and set text
				client.createPad(uuid);
				htmlBody = "<div>" + htmlBody + "</div>";
				htmlBody = StringEscapeUtils.unescapeHtml(htmlBody);
				client.setHTML(uuid, htmlBody);
			} catch (Exception e) {
				logger.info("Etherpad(" + uuid + ") already exist.");
			}
			return "redirect:http://edit.idocv.com/p/" + uuid;
		} catch (Exception e) {
			logger.error("Load editor error: ", e);
			return "{\"error\":" + e.getMessage() + "}";
		}
	}

}
