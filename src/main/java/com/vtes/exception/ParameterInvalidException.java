package com.vtes.exception;

public class ParameterInvalidException extends Exception {
	private static final long serialVersionUID = 1L;

	private String msg;

	public ParameterInvalidException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
