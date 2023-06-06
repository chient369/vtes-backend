package com.vtes.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginPayload {
	@NotBlank
	@Size(min = 16, max = 128)
	@Pattern(regexp = ".+@vti\\.com\\.vn$")
	private String email;

	@NotBlank
	@Size(min = 8, max = 64)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).*$")
	private String password;

	private boolean remember = false;

}
