package com.idocv.docview.service;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.vo.AudioVo;
import com.idocv.docview.vo.ExcelVo;
import com.idocv.docview.vo.ImgVo;
import com.idocv.docview.vo.PPTVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.PdfVo;
import com.idocv.docview.vo.TxtVo;
import com.idocv.docview.vo.WordVo;
import com.idocv.docview.vo.ZipVo;

/**
 * Office Preview Service
 * 
 * @author Godwin
 * @since 2012-10-22
 * @version 1.0
 * 
 */
public interface ViewService {

	static final String IMG_WIDTH_200 = "200";
	// static final String IMG_WIDTH_960 = "960";
	static final String IMG_WIDTH_1024 = "1024";
	// static final String IMG_WIDTH_1280 = "1280";
	static final String PDF_TO_HTML_TYPE = "html";
	static final String PDF_TO_IMAGE_TYPE = "png";

	/**
	 * Convert document(rid) to HTML.
	 * 
	 * @param rid
	 * @return
	 * @throws DocServiceException
	 */
	public boolean convert(String rid) throws DocServiceException;

	/**
	 * Word page.
	 * 
	 * @param rid
	 * @return
	 */
	PageVo<WordVo> convertWord2HtmlAll(String rid) throws DocServiceException;
	
	/**
	 * Word page.
	 * 
	 * @param rid
	 * @param start
	 * @param limit
	 * @return
	 */
	PageVo<WordVo> convertWord2Html(String rid, int start, int limit) throws DocServiceException;
	
	/**
	 * Word view (word -> pdf -> png).
	 * 
	 * @param rid
	 * @param start
	 * @param limit
	 * @return
	 */
	PageVo<WordVo> convertWord2Img(String rid, int start, int limit) throws DocServiceException;
	
	/**
	 * Word page.
	 * 
	 * @param rid
	 * @param start
	 * @param limit
	 * @return
	 */
	PageVo<PdfVo> convertWord2PdfStamp(String rid, String stamp, float xPercent, float yPercent) throws DocServiceException;

	/**
	 * A ExcelVo represents one Excel Sheet.
	 * 
	 * @param rid
	 * @param start starting sheet, 1-based.
	 * @param limit	how many Excel Sheet to be returned.
	 * @return
	 */
	PageVo<ExcelVo> convertExcel2Html(String rid, int start, int limit) throws DocServiceException;
	
	/**
	 * Excel view (excel -> pdf -> png).
	 * 
	 * @param rid
	 * @param start
	 * @param limit
	 * @return
	 */
	PageVo<ExcelVo> convertExcel2Img(String rid, int start, int limit) throws DocServiceException;

	/**
	 * A PPTVo is one PPT slide.
	 * 
	 * @param rid
	 * @param start starting slide, 1-based.
	 * @param limit	slide count to be returned.
	 * @return
	 */
	PageVo<PPTVo> convertPPT2Img(String rid, int start, int limit) throws DocServiceException;
	
	/**
	 * Get TXT content.
	 * 
	 * @param rid
	 */
	PageVo<TxtVo> convertTxt2Html(String rid, int start, int limit) throws DocServiceException;

	/**
	 * Convert PDF to Image file.
	 * 
	 * @param rid
	 * @return
	 */
	PageVo<PdfVo> convertPdf2Img(String rid, int start, int limit) throws DocServiceException;
	
	/**
	 * Convert PDF to HTML file.
	 * 
	 * @param rid
	 * @return
	 */
	PageVo<PdfVo> convertPdf2Html(String rid, int start, int limit) throws DocServiceException;

	/**
	 * Convert Image to Jpg file.
	 * 
	 * @param rid
	 * @return
	 */
	PageVo<ImgVo> convertImage2Jpg(String rid) throws DocServiceException;
	
	/**
	 * Convert Audio to MP3 file.
	 * 
	 * @param rid
	 * @return
	 */
	PageVo<AudioVo> convertAudio2Mp3(String rid) throws DocServiceException;

	/**
	 * Extract ZIP to files, support types including zip, rar, tar, 7z etc.
	 * 
	 * @param rid
	 * @return
	 * @throws DocServiceException
	 */
	PageVo<ZipVo> convertZip2File(String rid) throws DocServiceException;

	/**
	 * Validate client IP
	 * 
	 * @param ip
	 * @return
	 */
	boolean validateIp(String ip) throws DocServiceException;
}
