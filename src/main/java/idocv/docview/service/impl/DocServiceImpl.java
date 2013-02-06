package idocv.docview.service.impl;

import idocv.docview.common.DocServiceException;
import idocv.docview.common.Paging;
import idocv.docview.dao.DocDao;
import idocv.docview.po.DocPo;
import idocv.docview.service.DocService;
import idocv.docview.util.RcUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service("docService")
public class DocServiceImpl implements DocService {
	
	private static final Logger logger = LoggerFactory.getLogger(DocServiceImpl.class);

	@Resource
	private DocDao docDao;

	@Resource
	private RcUtil rcUtil;

	private static Set<String> docTypes = new HashSet<String>();

	static {
		docTypes.add("doc");
		docTypes.add("docx");
		docTypes.add("odt");	// OpenOffice Writer
		docTypes.add("xls");
		docTypes.add("xlsx");
		docTypes.add("ods");	// OpenOffice Spreadsheet
		docTypes.add("ppt");
		docTypes.add("pptx");
		docTypes.add("odp");	// OpenOffice Presentation
		docTypes.add("pdf");
		docTypes.add("txt");
	}

	@Override
	public DocPo save(String ip, String name, byte[] data) throws DocServiceException {
		if (StringUtils.isBlank(ip) || StringUtils.isBlank(name) || null == data || data.length <= 0) {
			throw new DocServiceException(0, "Insufficient parameter!");
		}
		DocPo doc = new DocPo();
		try {
			int size = data.length;
			String rid = RcUtil.genRid(ip, name, size);
			doc.setRid(rid);
			doc.setName(name);
			doc.setSize(String.valueOf(size));
			doc.setCtime(System.currentTimeMillis());
			
			// save file meta and file
			FileUtils.writeByteArrayToFile(new File(rcUtil.getPath(rid)), data);

			// save info
			docDao.add(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	@Override
	public DocPo saveUrl(String ip, String url, String name) throws DocServiceException {
		if (StringUtils.isBlank(ip) || StringUtils.isBlank(name) || StringUtils.isBlank(url)) {
			throw new DocServiceException(0, "Insufficient parameter!");
		}
		DocPo po = docDao.getUrl(url);
		if (null != po) {
			return po;
		}
		String host = getHost(url);
		Response urlResponse = null;
		try {
			urlResponse = Jsoup.connect(url).referrer(host).userAgent("Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreContentType(true).execute();
			byte[] bytes = urlResponse.bodyAsBytes();
			po = save(ip, name, bytes);
			docDao.updateUrl(po.getRid(), url);
			return po;
		} catch (IOException e) {
			logger.error("save url doc error: ", e);
			throw new DocServiceException("saveUrl error: ", e);
		}
	}

	@Override
	public boolean delete(String rid) throws DocServiceException {
		return docDao.delete(rid);
	}

	@Override
	public DocPo get(String rid) throws DocServiceException {
		return docDao.get(rid);
	}

	@Override
	public DocPo getUrl(String url) throws DocServiceException {
		return docDao.getUrl(url);
	}

	@Override
	public Paging<DocPo> list(int start, int length) throws DocServiceException {
		return docDao.list(start, length);
	}

	@Override
	public int count() throws DocServiceException {
		return docDao.count();
	}

	public static String getHost(String url) throws DocServiceException {
		return url.replaceFirst("((http[s]?)?(://))?([^/]*)(/?.*)", "$4");
	}
}
