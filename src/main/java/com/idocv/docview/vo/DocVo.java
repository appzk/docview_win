package com.idocv.docview.vo;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DocVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5796209533499028715L;

	/**
	 * 资源id
	 */
	private String rid;

	/**
	 * UUID
	 */
	private String uuid;

	/**
	 * 应用id
	 */
	private String appId;

	/**
	 * 资源名称
	 */
	private String name;

	/**
	 * 资源大小
	 */
	private long size;

	/**
	 * 状态，0. 刚上传默认; -1. 已经删除;
	 */
	private int status;

	/**
	 * 创建时间
	 */
	private long ctime;

	/**
	 * 更新时间
	 */
	private long utime;

	/**
	 * 资源扩展名
	 */
	private String ext;

	/**
	 * 通过url上传资源时的URL
	 */
	private String url;

	/**
	 * preview count
	 * 
	 * @return
	 */
	private int viewCount;

	/**
	 * download count
	 * 
	 * @return
	 */
	private int downloadCount;

	/**
	 * access mode, 0-default, can be viewed, 1-private, 2-semi-public, 3-public
	 */
	private int mode;

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}

	public long getUtime() {
		return utime;
	}

	public void setUtime(long utime) {
		this.utime = utime;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
