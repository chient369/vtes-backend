package com.vtes.exception;

public class NotFoundException extends VtesException {
	private static final long serialVersionUID = 1L;

	public NotFoundException(String code, String message) {
		super(code, message);
	}
	public NotFoundException(String message) {
		super(message);
	}


}
