package com.idocv.docview.common;

import java.io.Serializable;
import java.util.List;

public class Paging<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 747819485262638654L;

	// init params
	private int iTotalDisplayRecords;
	private int iTotalRecords;
	private List<T> aaData;

	// display params
	private int iDisplayStart;
	private int iDisplayLength; // page size

	public Paging(List<T> list, int total) {
		if (null == list) {
			return;
		}
		this.iTotalDisplayRecords = total;
		this.iTotalRecords = total;
		this.aaData = list;
	}

	public int getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public int getiTotalRecords() {
		return iTotalRecords;
	}

	public void setiTotalRecords(int iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}

	public List<T> getAaData() {
		return aaData;
	}

	public void setAaData(List<T> aaData) {
		this.aaData = aaData;
	}

	public int getiDisplayStart() {
		return iDisplayStart;
	}

	public void setiDisplayStart(int iDisplayStart) {
		this.iDisplayStart = iDisplayStart;
	}

	public int getiDisplayLength() {
		return iDisplayLength;
	}

	public void setiDisplayLength(int iDisplayLength) {
		this.iDisplayLength = iDisplayLength;
	}
}
