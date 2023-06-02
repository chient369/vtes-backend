package com.vtes.payload;

import javax.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterPayload {
	@NotBlank
	@Size(min = 6, max = 128)
	private String fullName;

	@NotNull
	@Min(value = 1)
	@Max(value = 999)
	private int departmentId;

	@NotBlank

	@Pattern(regexp = ".+@vti\\.com\\.vn$")
	private String email;

	@NotBlank
	@Size(min = 8, max = 64)
	private String password;

}
