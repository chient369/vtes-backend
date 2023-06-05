package com.vtes.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class EmailPayload {
	@NotBlank
	@Size(min = 16, max = 128)
	@Pattern(regexp = ".+@vti\\.com\\.vn$")
	private String email;

}
