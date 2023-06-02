package com.vtes.exception;

public class UserException extends VtesException{

	private static final long serialVersionUID = 1L;
	
	public UserException(String code, String message) {
		super(code, message);
	}

}
