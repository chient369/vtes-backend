package com.vtes.exception;

public class FareNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;

	public FareNotFoundException(String msg) {
		super(msg);
		this.message = msg;
	}

	public String getMsg() {
		return message;
	}

	public void setMsg(String msg) {
		this.message = msg;
	}
	

}
