package com.vtes.exception;

public class AuthenticationFailedException extends VtesException{
	private static final long serialVersionUID = 1L;
	
	public AuthenticationFailedException(String code, String message) {
		super(code, message);
	}
	public AuthenticationFailedException(String email) {
		super("API001_ER01",String.format("Email or password invalid with email: %s", email));

	}
	
}
