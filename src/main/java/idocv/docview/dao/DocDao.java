package idocv.docview.dao;

import idocv.docview.common.Paging;
import idocv.docview.po.DocPo;

public interface DocDao {

	/**
	 * Save a Document.
	 * 
	 * @param doc
	 */
	void add(DocPo doc);

	/**
	 * Delete a document.
	 */
	boolean delete(String rid);

	/**
	 * Update Url.
	 * 
	 * @param rid
	 * @param url
	 * @return
	 */
	boolean updateUrl(String rid, String url);

	/**
	 * get Doc by rid.
	 * 
	 * @param rid
	 * @return
	 */
	DocPo get(String rid);
	
	/**
	 * get Doc by URL.
	 * 
	 * @param url
	 * @return
	 */
	DocPo getUrl(String url);

	/**
	 * get Doc list by rid array.
	 * 
	 * @param rids
	 * @return
	 */
	Paging<DocPo> list(int start, int length);

	/**
	 * get total count.
	 */
	int count();
}
