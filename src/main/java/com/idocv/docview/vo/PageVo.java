package com.idocv.docview.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PageVo<T> implements Serializable {

	private int code = 1;
	private String Desc = "Success";

	private String name;
	private String rid;
	private String uuid;
	private String md5;
	private String url;
	private int totalSize;
	private int curPage = 1;
	private int totalPage;
	private int pageSize = 10;
	private List<String> titles;
	private List<T> data;
	private String styleUrl;

	private PageVo() {
		
	}
	
	public PageVo(List<T> data, int totalSize) {
		this.data = data;
		if (totalSize >= 0) {
			this.totalSize = totalSize;
		} else {
			this.totalSize = 0;
		}
		this.totalPage = (totalSize + this.pageSize - 1) / this.pageSize;
	}

	public PageVo(List<T> data, int totalSize, int curPage, int pageSize) {
		this.data = data;
		if (totalSize >= 0) {
			this.totalSize = totalSize;
		} else {
			this.totalSize = 0;
		}
		if (pageSize > 0) {
			this.pageSize = pageSize;
		}
		curPage = curPage > 0 ? curPage : 1;
		this.curPage = curPage;
		this.totalPage = (totalSize + this.pageSize - 1) / this.pageSize;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
	
	public String getStyleUrl() {
		return styleUrl;
	}

	public void setStyleUrl(String styleUrl) {
		this.styleUrl = styleUrl;
	}

	public static PageVo getErrorPageVo(String message) {
		PageVo page = new PageVo();
		page.setCode(0);
		page.setDesc(message);
		return page;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("aaa");
		list.add("bbb");
		list.add("ccc");
		PageVo<String> page = new PageVo<String>(list, 45);
		System.out.println(page);
	}
}