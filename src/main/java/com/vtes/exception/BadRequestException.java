package com.vtes.exception;

public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String msg;

	public BadRequestException(String msg) {
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
