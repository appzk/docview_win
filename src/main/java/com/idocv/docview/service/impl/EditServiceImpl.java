package com.idocv.docview.service.impl;


import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.EditService;
import com.idocv.docview.service.PreviewService;
import com.idocv.docview.vo.DocVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.WordVo;


@Service
public class EditServiceImpl implements EditService {
	
	private static final Logger logger = LoggerFactory.getLogger(EditServiceImpl.class);

	@Resource
	private DocService docService;

	@Resource
	private PreviewService previewService;

	@Override
	public String getHtmlBody(String uuid) throws DocServiceException {
		// 1. get docVo by uuid
		DocVo docVo = docService.getByUuid(uuid);
		if (null == docVo || StringUtils.isBlank(docVo.getRid())) {
			logger.error("Document(" + uuid + ") NOT found!");
			throw new DocServiceException("Document(" + uuid + ") NOT found!");
		}
		String rid = docVo.getRid();
		PageVo<WordVo> wordPage = previewService.convertWord2Html(rid, 0, 0);
		List<WordVo> pageData = wordPage.getData();
		if (CollectionUtils.isEmpty(pageData)) {
			logger.error("Document(" + uuid + ") content is empty!");
			throw new DocServiceException("Document(" + uuid + ") content is empty!");
		}
		StringBuilder sb = new StringBuilder();
		for (WordVo vo : pageData) {
			String content = vo.getContent();
			sb.append(content);
		}
		String cleanText = Jsoup.clean(sb.toString(), Whitelist.basic());
		return cleanText;
	}

}