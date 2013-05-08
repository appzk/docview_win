package com.idocv.docview.vo;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SessionVo implements Serializable {

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
	private String ctime;

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

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}