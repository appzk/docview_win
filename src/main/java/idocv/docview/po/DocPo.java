package idocv.docview.po;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DocPo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5796209533499028715L;

	/**
	 * 资源id
	 */
	private String rid;

	/**
	 * 资源名称
	 */
	private String name;

	/**
	 * 资源大小
	 */
	private String size;

	/**
	 * 创建时间
	 */
	private long ctime;

	/**
	 * 资源扩展名
	 */
	private String ext;

	/**
	 * 通过url上传资源时的URL
	 */
	private String url;

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
