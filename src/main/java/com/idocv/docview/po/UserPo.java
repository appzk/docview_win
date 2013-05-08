package com.idocv.docview.po;

import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;

public class UserPo {

	private String id;
	private String appId;
	private String username;
	private String password;
	private String email;
	private String ctime;

	/**
	 * 用户状态，0-刚注册，1-已验证邮箱，-1-已删除，100-该应用的管理员
	 */
	private int status;
	private Collection<String> sids;

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

	public Collection<String> getSids() {
		return sids;
	}

	public void setSids(Collection<String> sids) {
		this.sids = sids;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
