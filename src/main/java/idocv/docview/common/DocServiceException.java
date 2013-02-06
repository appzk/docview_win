package idocv.docview.common;

public class DocServiceException extends Exception {

	private static final long serialVersionUID = 6831572063270658235L;

	private int code;

	private String desc;

	public DocServiceException(int code, String desc) {
		super(desc);
		this.code = code;
		this.desc = desc;
	}

	public DocServiceException(String message, Throwable cause) {
		super(message, cause);
		this.code = 0;
		this.desc = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}