package com.idocv.docview.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

public class PDFUtil {
	public static void main(String[] args) {
		try {
			// File pdf = new File("/Users/Godwin/test.pdf");
			File pdf = new File("d:/a.pdf");
			File pdfFileModified = new File("/Users/Godwin/test-modified.pdf");

			Map<String, String> metas = new HashMap<String, String>();
			metas.put("x", "200");
			metas.put("y", "300");
			metas.put("page", "1");
			metas.put("md5", getMd5(pdf));
			// setPDFMeta(pdf, metas);


			System.out.println("METAS: \n");
			metas = getPDFMetas(pdf);
			for (Entry<String, String> entry : metas.entrySet()) {
				System.out.println(entry.getKey() + " - " + entry.getValue());
			}

			PDDocument doc = PDDocument.load(pdf);
			PDDocumentInformation info = doc.getDocumentInformation();
			System.out.println("MOD: " + info.getModificationDate());
			System.out.println("CRE: " + info.getCreationDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * get md5 of file
	 * 
	 * @param file
	 * @return
	 */
	public static String getMd5(File file) {
		if (null == file || !file.isFile()) {
			return null;
		}
		try {
			return DigestUtils.md5Hex(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Set PDF meta
	 * 
	 * @param pdf
	 * @param name
	 * @param value
	 */
	public static void setPDFMeta(File pdf, String name, String value) {
		try {
			PDDocument doc = PDDocument.load(pdf);
			PDDocumentInformation info = doc.getDocumentInformation();
			info.setCustomMetadataValue(name, value);
			doc.save(pdf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set PDF metas
	 * 
	 * @param pdf
	 * @param name
	 * @param value
	 */
	public static void setPDFMeta(File pdf, Map<String, String> metas) {
		try {
			PDDocument doc = PDDocument.load(pdf);
			PDDocumentInformation info = doc.getDocumentInformation();
			for (Entry<String, String> entry : metas.entrySet()) {
				info.setCustomMetadataValue(entry.getKey(), entry.getValue());
			}
			doc.save(pdf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * get PDF meta
	 * 
	 * @param pdf
	 * @param name
	 * @return
	 */
	public static String getPDFMeta(File pdf, String name) {
		try {
			PDDocument doc = PDDocument.load(pdf);
			PDDocumentInformation info = doc.getDocumentInformation();
			return info.getCustomMetadataValue(name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * get all PDF metas
	 * 
	 * @param pdf
	 * @return
	 */
	public static Map<String, String> getPDFMetas(File pdf) {
		try {
			PDDocument doc = PDDocument.load(pdf);
			PDDocumentInformation info = doc.getDocumentInformation();
			Set<String> keys = info.getMetadataKeys();
			Map<String, String> metas = new HashMap<String, String>();
			for (String key : keys) {
				metas.put(key, info.getCustomMetadataValue(key));
			}
			return metas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}