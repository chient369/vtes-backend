package com.vtes.exception;

public class CommuterPassNotFound extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CommuterPassNotFound(Integer userId) {
		super(String.format("Not Found commuterPass with user ID [%d]", userId));
}
	
	

}
