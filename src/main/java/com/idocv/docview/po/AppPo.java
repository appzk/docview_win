package com.idocv.docview.po;

import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Application Po<br>
 * Necessary fields: 1. id; 2. name; 3. key; 4. phone.
 * 
 * @author Godwin
 * 
 */
public class AppPo {

	/**
	 * Application id, should be 3 characters
	 */
	private String id;

	/**
	 * Application name
	 */
	private String name;

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
	 * Contact address
	 */
	private String address;

	/**
	 * Create time
	 */
	private long ctime;
	
	/**
	 * Update time
	 */
	private long utime;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}