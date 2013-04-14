package com.idocv.docview.vo;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class LabelVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5534440053141939473L;

	/**
	 * 标签id
	 */
	private String id;

	/**
	 * uid
	 */
	private String uid;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 显示名称
	 */
	private String value;

	/**
	 * 状态，0. 正常; -1. 已经删除;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}