package com.vtes.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ResetPasswordPayload {
	@NotBlank
	@Size(min = 8, max = 64)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).*$")
	private String newPassword;

	@NotNull
	@Size(min = 64, max = 256)
	private String authToken;
}
