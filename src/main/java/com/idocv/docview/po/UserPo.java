package com.idocv.docview.po;

import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;

public class UserPo {

	private String id;
	private String appId;
	private String username;
	private String password;
	private String email;
	private long ctime;
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

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
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
