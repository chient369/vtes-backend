package com.vtes.controller;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.vtes.exception.CommuterPassNotFound;
import com.vtes.exception.FareNotFoundException;
import com.vtes.exception.ParameterInvalidException;
import com.vtes.exception.TokenRefreshException;
import com.vtes.exception.UploadFileException;
import com.vtes.payload.response.ResponseData;
import com.vtes.payload.response.ResponseData.ResponseType;

@RestControllerAdvice
public class ApiExceptionController {
	private Logger LOGGER = LoggerFactory.getLogger(ApiExceptionController.class);

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseData badRequestResponse(Exception ex) {
		LOGGER.error(ex.getMessage());
		return ResponseData.builder()
				.code("500")
				.message("Server error")
				.type(ResponseType.ERROR)
				.build();
		
	}
	
	@ExceptionHandler(value = TokenRefreshException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseData handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
		return ResponseData.builder()
				.type(ResponseType.ERROR)
				.code("403")
				.message(ex.getMessage())
				.build();
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseData methodArgumentTypeMissmatch(Exception ex) {
		return ResponseData.builder()
				.code("405")
				.message(ex.getMessage())
				.type(ResponseType.ERROR)
				.build();
		
	}
	
	@ExceptionHandler(ParameterInvalidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseData invalidParamExResponse(Exception ex) {
		return ResponseData.builder()
				.code("API_ER02")
				.message("Invalid parameter")
				.type(ResponseType.ERROR)
				.build();
		
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseData bodyRequestAgrgumentInvalid(Exception ex) {
		return ResponseData.builder()
				.code("API_ER03")
				.message("Invalid request body of data")
				.type(ResponseType.ERROR)
				.build();
		
	}
	
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	public ResponseData methodNotAllowException(Exception ex) {
		return ResponseData.builder()
				.code("API_ER03")
				.message("Method not allow")
				.type(ResponseType.ERROR)
				.build();
		
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseData missingRequestParameterResponse(Exception ex) {
		return ResponseData.builder()
				.code("API_ER02")
				.message(ex.getMessage())
				.type(ResponseType.ERROR)
				.build();
		
	}
	
	@ExceptionHandler(FareNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ResponseData fareNotFoundException(FareNotFoundException ex) {
		return ResponseData.builder()
				.code("API010_ER")
				.message("Not found fare record")
				.type(ResponseType.WARINING)
				.build();
		
	}
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
	public ResponseData maxUploadSizeExceoption(Exception ex) {
		return ResponseData.builder()
				.code("API006_ER")
				.message("This file size is too large")
				.type(ResponseType.ERROR)
				.build();
		
	}
	
	@ExceptionHandler(UploadFileException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseData uploadFileError(Exception ex) {
		return ResponseData.builder()
				.code("API_ER03")
				.message(ex.getMessage())
				.type(ResponseType.ERROR)
				.build();
		
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseData notFoundResource(EntityNotFoundException ex) {
		return ResponseData.builder()
				.code("404")
				.message(ex.getMessage())
				.type(ResponseType.ERROR)
				.build();
		
	}
	@ExceptionHandler(CommuterPassNotFound.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseData notFoundCommuterPass(Exception ex) {
		return ResponseData.builder()
				.code("API008_ER")
				.message(ex.getMessage())
				.type(ResponseType.ERROR)
				.build();
		
	}
	
}
