package com.vtes.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UserActiveRequest {
	@NotBlank
	private String verifyCode;
}
