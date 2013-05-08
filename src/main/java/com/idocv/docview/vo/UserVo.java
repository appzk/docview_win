package com.idocv.docview.vo;

import org.apache.commons.lang.builder.ToStringBuilder;

public class UserVo {

	private String id;
	private String app;
	private String username;
	private String password;
	private String email;
	private String ctime;

	/**
	 * 用户状态，0-刚注册，1-已验证邮箱，-1-已删除
	 */
	private int status;
	private String sid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}