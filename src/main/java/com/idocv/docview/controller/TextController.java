package com.idocv.docview.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idocv.docview.common.ViewType;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.service.AppService;
import com.idocv.docview.service.DocService;
import com.idocv.docview.service.SessionService;
import com.idocv.docview.service.TextService;
import com.idocv.docview.service.ViewService;
import com.idocv.docview.util.RcUtil;
import com.idocv.docview.vo.DocVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.SessionVo;
import com.idocv.docview.vo.ViewBaseVo;

@Controller
@RequestMapping("text")
public class TextController {
	
	private static final Logger logger = LoggerFactory.getLogger(TextController.class);
	
	@Resource
	private AppService appService;

	@Resource
	private DocService docService;

	@Resource
	private ViewService viewService;

	@Resource
	private TextService textService;

	@Resource
	private SessionService sessionService;

	private @Value("${thd.view.template}")
	String thdViewTemplate;

	private @Value("${view.page.load.async}")
	boolean pageLoadAsync;
	
	private @Value("${view.page.word.style}")
	String pageWordStyle;
	
	private @Value("${view.page.excel.style}")
	String pageExcelStyle;

	@Resource
	private RcUtil rcUtil;

	/**
	 * get document content by uuid in json format
	 * 
	 *  1. get docVo by uuid
	 *	2. check access mode of docVo
	 *	3. public mode -> direct view
	 *	4. semi-public | private mode -> 5
	 *	5. get sessionVo by sessionId
	 *	6. current time - ctime > expire time ? session expired : view.
	 *			PageVo<? extends Serializable> page = null;
	 * 
	 */
	@RequestMapping("{uuid}.json")
	@ResponseBody
	public PageVo<? extends Serializable> jsonUuid(HttpServletRequest req,
			@RequestParam(defaultValue = "default") String template,
			@PathVariable String uuid,
			@RequestParam(defaultValue = "1") int start,
			@RequestParam(defaultValue = "5") int size) {
		PageVo<? extends Serializable> page = null;
		String rid = null;
		String session = null;
		try {
			if (uuid.matches("\\w{24}")) {
				// session id
				SessionVo sessionVo = sessionService.get(uuid);
				if (null == sessionVo) {
					throw new DocServiceException("会话不存在！");
				}
				uuid = sessionVo.getUuid();
				session = sessionVo.getId();
			}

			// 1. get docVo by uuid
			DocVo docVo = docService.getByUuid(uuid);
			if (null == docVo || StringUtils.isBlank(docVo.getRid())) {
				throw new DocServiceException("文档(" + uuid + ")不存在！");
			}
			rid = docVo.getRid();
			String ext = RcUtil.getExt(rid);

			// 2. check access mode of docVo
			int accessMode = docVo.getStatus();
			if (0 == accessMode) {
				if (StringUtils.isBlank(session)) {
					throw new DocServiceException("私有文档不能公开访问，请使用会话id来访问！");
				}
				// 5. get sessionVo by sessionId
				SessionVo sessionVo = sessionService.get(session);
				if (null == sessionVo) {
					throw new DocServiceException("会话不存在！");
				}
				// 6. current time - ctime > expire time ? session expired :
				// view.
				String sessionCtimeString = sessionVo.getCtime();
				long sessionCtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionCtimeString).getTime();
				long currentTime = System.currentTimeMillis();
				if (currentTime - sessionCtime > 3600000) {
					throw new DocServiceException("会话已过期，请重新获取一个会话！");
				}
				if (!uuid.equals(sessionVo.getUuid())) {
					throw new DocServiceException("该会话和文档不一致，无法预览！");
				}
			}
			if (ViewType.WORD == ViewType.getViewType(ext)) {
				start = (start - 1) * size + 1;
				page = textService.getWordText(rid, start, size);
			} else if (ViewType.EXCEL == ViewType.getViewType(ext)) {
				if ("pdf".equalsIgnoreCase(pageExcelStyle)) {
					page = viewService.convertExcel2Img(rid, 1, 0);
				} else {
					page = viewService.convertExcel2Html(rid, start, size);
				}
			} else if (ViewType.PPT == ViewType.getViewType(ext)) {
				page = viewService.convertPPT2Img(rid, start, size);
			} else if (ViewType.TXT == ViewType.getViewType(ext)) {
				start = (start - 1) * size + 1;
				page = viewService.convertTxt2Html(rid, start, size);
			} else if (ViewType.PDF == ViewType.getViewType(ext)) {
				// page = previewService.convertPdf2Html(rid, 1, 0);
				page = viewService.convertPdf2Img(rid, 1, 0);
			} else if (ViewType.IMG == ViewType.getViewType(ext)) {
				page = viewService.convertImage2Jpg(rid);
			} else {
				page = new PageVo<ViewBaseVo>(null, 0);
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