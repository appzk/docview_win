package com.idocv.docview.po;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DocPo implements Serializable {

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
	 * 所属标签列表
	 */
	private List<String> labels;

	/**
	 * 状态，0. 刚上传默认; -1. 已经删除;
	 */
	private int status;

	/**
	 * 创建时间
	 */
	private String ctime;

	/**
	 * 更新时间
	 */
	private String utime;

	/**
	 * 资源扩展名
	 */
	private String ext;

	/**
	 * 通过url上传资源时的URL
	 */
	private String url;

	/**
	 * preview log
	 * 
	 * @return
	 */
	private List<Long> viewLog;

	/**
	 * download log
	 * 
	 * @return
	 */
	private List<Long> downloadLog;

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

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getUtime() {
		return utime;
	}

	public void setUtime(String utime) {
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

	public List<Long> getViewLog() {
		return viewLog;
	}

	public void setViewLog(List<Long> viewLog) {
		this.viewLog = viewLog;
	}

	public List<Long> getDownloadLog() {
		return downloadLog;
	}

	public void setDownloadLog(List<Long> downloadLog) {
		this.downloadLog = downloadLog;
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
