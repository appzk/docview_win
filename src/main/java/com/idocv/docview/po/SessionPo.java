package com.idocv.docview.po;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SessionPo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7700031964305823224L;

	/**
	 * session id
	 */
	private String id;

	/**
	 * app id
	 */
	private String appId;

	/**
	 * uuid
	 */
	private String uuid;

	/**
	 * create time
	 */
	private long ctime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}