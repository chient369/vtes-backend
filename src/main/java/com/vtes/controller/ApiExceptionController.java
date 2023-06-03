package com.vtes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.vtes.exception.AuthenticationFailedException;
import com.vtes.exception.NotFoundException;
import com.vtes.exception.ParameterInvalidException;
import com.vtes.exception.TokenRefreshException;
import com.vtes.exception.UploadFileException;
import com.vtes.exception.UserException;
import com.vtes.exception.VtesException;
import com.vtes.model.ResponseData;
import com.vtes.model.ResponseData.ResponseType;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ApiExceptionController {

	@ExceptionHandler(VtesException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseData badRequestResponse(VtesException ex) {
		log.debug("{}",ex.getMessage());
		return ResponseData.builder()
				.code(ex.getCode()== null ? "":ex.getCode())
				.message(ex.getMessage())
				.type(ResponseType.ERROR)
				.build();

	}
	
	
	@ExceptionHandler(UserException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseData userOfExceptionHandler(UserException ex) {
		return ResponseData.builder()
				.code(ex.getCode())
				.message(ex.getMessage())
				.type(ResponseType.ERROR)
				.build();
	}
	

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ResponseData notFoundCommuterPassValid(VtesException ex) {
		log.debug("Error: {}",ex.getMessage());
		return ResponseData.builder()
				.code(ex.getCode())
				.message(ex.getMessage())
				.type(ResponseType.ERROR)
				.build();

	}
	@ExceptionHandler(AuthenticationFailedException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public ResponseData authenFailedException(AuthenticationFailedException ex) {
		return ResponseData.builder()
				.code(ex.getCode())
				.message(ex.getMessage())
				.type(ResponseType.ERROR)
				.build();

	}

	@ExceptionHandler(value = TokenRefreshException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseData handleTokenRefreshException(TokenRefreshException ex) {
		log.debug("{}",ex.getMessage());
		return ResponseData.builder()
				.type(ResponseType.ERROR)
				.code("")
				.message(ex.getMessage())
				.build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseData methodArgumentTypeMissmatch(Exception ex) {
		log.debug("{}",ex.getMessage());
		return ResponseData.builder()
				.code("API_ER02")
				.message(ex.getMessage())
				.type(ResponseType.ERROR).build();

	}

	@ExceptionHandler(ParameterInvalidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseData invalidParamExResponse(Exception ex) {
		log.debug("{}",ex.getMessage());
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
				.code("")
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
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
	public ResponseData maxUploadSizeExceoption(Exception ex) {
		log.debug("{}",ex.getMessage());
		return ResponseData.builder()
				.code("API006_ER")
				.message("This file size is too large")
				.type(ResponseType.ERROR)
				.build();

	}

	@ExceptionHandler(UploadFileException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseData uploadFileError(VtesException ex) {
		log.debug("{}",ex.getMessage());
		return ResponseData.builder()
				.code(ex.getCode())	
				.message(ex.getMessage())
				.type(ResponseType.ERROR)
				.build();

	}
}
