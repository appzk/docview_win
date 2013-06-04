package com.idocv.docview.vo;

import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Application Vo<br>
 * Necessary fields: 1. id; 2. name; 3. key; 4. phone.
 * 
 * @author Godwin
 * 
 */
public class AppVo {

	/**
	 * Application id, should be 3 characters
	 */
	private String id;

	/**
	 * Application name
	 */
	private String name;

	/**
	 * Application logo URL
	 */
	private String logo;

	/**
	 * Secret key, must starts with {id}
	 */
	private String key;

	/**
	 * Authorized IP addresses, the first one is the main IP
	 */
	private Collection<String> ips;

	/**
	 * Contact phone number
	 */
	private String phone;

	/**
	 * Email
	 */
	private String email;

	/**
	 * Contact address
	 */
	private String address;

	/**
	 * Create time
	 */
	private String ctime;
	
	/**
	 * Update time
	 */
	private String utime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Collection<String> getIps() {
		return ips;
	}

	public void setIps(Collection<String> ips) {
		this.ips = ips;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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