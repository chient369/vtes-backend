package com.vtes.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class ResponseData {	
	public enum ResponseType {
		INFO,WARINING,ERROR;
	}
	private ResponseType type;
	private String message;
	private String code;
	private Object data;
	

}

