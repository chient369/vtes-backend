package com.vtes.exception;

import com.amazonaws.services.s3.model.AmazonS3Exception;

public class UploadFileException extends AmazonS3Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UploadFileException(String fileName) {
		super(String.format("Upload File [%s] error", fileName));
}
	
	

}
