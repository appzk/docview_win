package com.idocv.docview.exception;

public class DBException extends Exception {

	private static final long serialVersionUID = 6871461573801164039L;

	public DBException() {
		super();
	}

	public DBException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBException(String message) {
		super(message);
	}

	public DBException(Throwable cause) {
		super(cause);
	}

}