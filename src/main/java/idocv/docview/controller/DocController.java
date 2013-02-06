package idocv.docview.controller;

import idocv.docview.common.DocResponse;
import idocv.docview.common.DocServiceException;
import idocv.docview.common.IpUtil;
import idocv.docview.common.Paging;
import idocv.docview.po.DocPo;
import idocv.docview.service.DocService;
import idocv.docview.service.PreviewService;
import idocv.docview.util.RcUtil;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("doc")
public class DocController {
	
	private static final Logger logger = LoggerFactory.getLogger(DocController.class);

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
	@RequestMapping("add")
	public DocResponse<DocPo> add(HttpServletRequest req,
			@RequestParam(value = "file", required = true) MultipartFile file) {
		DocResponse<DocPo> resp = new DocResponse<DocPo>();
		try {

			String ip = IpUtil.getIpAddr(req);

			if (!previewService.validateIp(ip)) {
				System.err.println("IP: " + ip);
				// TODO
			}
			byte[] data = file.getBytes();
			String name = file.getOriginalFilename();
			DocPo po = docService.save(ip, name, data);
			logger.info("--> " + ip + " ADD " + po.getRid());
			System.err.println("--> " + ip + " ADD " + po.getRid());
			return resp.getSuccessResponse(po);
		} catch (Exception e) {
			logger.error("upload error <controller>: ", e);
			return resp.getFailResponse(0, e.getMessage());
		}
	}
	
	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("delete")
	public String delete(@RequestParam(value = "id", required = true) String id) {
		try {
			boolean result = docService.delete(id);
			System.out.println("Result: " + result);
		} catch (Exception e) {
			logger.error("upload error <controller>: ", e);
		}
		return "redirect:/";
	}

	/**
	 * 页面列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping("list")
	public Paging<DocPo> list(
			@RequestParam(value = "iDisplayStart", defaultValue = "0") Integer start,
			@RequestParam(value = "iDisplayLength", defaultValue = "10") Integer length) {
		try {
			Paging<DocPo> list = docService.list(start, length);
			return list;
		} catch (DocServiceException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 下载
	 */
	@RequestMapping("download")
	public void download(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "id") String id) {
		try {
			DocPo po = docService.get(id);
			String rid = po.getRid();
			String path = rcUtil.getPath(rid);
			DocResponse.setResponseHeaders(req, resp, po.getName());
			IOUtils.write(FileUtils.readFileToByteArray(new File(path)), resp.getOutputStream());
		} catch (Exception e) {
			logger.error("download error: ", e);
		}
	}
}
