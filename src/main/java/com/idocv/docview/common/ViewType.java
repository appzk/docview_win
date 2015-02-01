package com.idocv.docview.common;

public enum ViewType {
	
	WORD("w"),
	EXCEL("x"),
	PPT("p"),
	PDF("f"),
	TXT("t"),
	IMG("i"),
	AUDIO("a"),
	VIDEO("v"),
	OTHER("o");

	private String symbol;
	
	private ViewType(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return this.symbol;
	}
}