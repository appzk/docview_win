package com.idocv.docview.service;

import java.util.ArrayList;
import java.util.List;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.vo.ExcelVo;
import com.idocv.docview.vo.PPTVo;
import com.idocv.docview.vo.PageVo;
import com.idocv.docview.vo.TxtVo;
import com.idocv.docview.vo.WordVo;

/**
 * Office Preview Service
 * 
 * @author Godwin
 * @since 2012-10-22
 * @version 1.0
 * 
 */
public interface ViewService {

	static List<String> convertingRids = new ArrayList<String>();

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
	 * @param start
	 * @param limit
	 * @return
	 */
	PageVo<WordVo> convertWord2Html(String rid, int start, int limit) throws DocServiceException;

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
	 * A PPTVo is one PPT slide.
	 * 
	 * @param rid
	 * @param start starting slide, 1-based.
	 * @param limit	slide count to be returned.
	 * @return
	 */
	PageVo<PPTVo> convertPPT2Html(String rid, int start, int limit) throws DocServiceException;
	
	/**
	 * Get TXT content.
	 * 
	 * @param rid
	 */
	PageVo<TxtVo> convertTxt2Html(String rid, int start, int limit) throws DocServiceException;

	/**
	 * Convert PDF to HTML file.
	 * 
	 * @param rid
	 * @return URL of the converted SWF
	 */
	String convertPdf2Html(String rid) throws DocServiceException;

	/**
	 * Validate client IP
	 * 
	 * @param ip
	 * @return
	 */
	boolean validateIp(String ip) throws DocServiceException;
}
