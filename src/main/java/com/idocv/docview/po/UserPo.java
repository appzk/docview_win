package com.idocv.docview.po;

import org.apache.commons.lang.builder.ToStringBuilder;

public class UserPo {

	private String id;
	private String app;
	private String username;
	private String password;
	private String email;
	private long ctiem;

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

	public long getCtiem() {
		return ctiem;
	}

	public void setCtiem(long ctiem) {
		this.ctiem = ctiem;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
