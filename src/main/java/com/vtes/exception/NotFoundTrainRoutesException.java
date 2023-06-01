package com.vtes.exception;

public class NotFoundTrainRoutesException extends Exception{

	private static final long serialVersionUID = 1L;

	public NotFoundTrainRoutesException() {
		super("This specifed routes not found");
	}

	
}
