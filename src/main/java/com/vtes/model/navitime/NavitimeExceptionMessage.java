package com.vtes.model.navitime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NavitimeExceptionMessage {
	    private int status_code;
	    private String message;
}
