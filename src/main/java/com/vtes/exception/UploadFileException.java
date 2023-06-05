package com.vtes.exception;

public class UploadFileException extends VtesException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UploadFileException(String fileName) {
		super("API_ER",String.format("Upload File [%s] error", fileName));
}
	
	

}
