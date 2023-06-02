package com.vtes.exception;

public class AuthenticationFailedException extends VtesException{
	private static final long serialVersionUID = 1L;
	
	public AuthenticationFailedException(String code, String message) {
		super(code, message);
	}
	public AuthenticationFailedException(String email) {
		super(String.format("Authenticated failed with email : %s", email));

	}


	

	
}
