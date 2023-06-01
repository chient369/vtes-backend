package com.vtes.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class PasswordResetRequest {
	@NotBlank
	@Size(min = 8, max = 64)
	private String newPassword;

	@NotNull
	@Size(min = 64, max = 256)
	private String authToken;
}
