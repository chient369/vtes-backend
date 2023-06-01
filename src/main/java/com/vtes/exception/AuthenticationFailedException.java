package com.vtes.exception;

public class AuthenticationFailedException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public AuthenticationFailedException(String email) {
		super(String.format("Authenticated failed with email : %s", email));

	}
}
