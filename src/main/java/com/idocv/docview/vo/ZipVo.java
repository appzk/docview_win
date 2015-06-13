package com.idocv.docview.vo;

public class ZipVo extends ViewBaseVo {

	/**
	 * Local fullpath
	 */
	private String path;
	private boolean isViewable;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isViewable() {
		return isViewable;
	}

	public void setViewable(boolean isViewable) {
		this.isViewable = isViewable;
	}
}