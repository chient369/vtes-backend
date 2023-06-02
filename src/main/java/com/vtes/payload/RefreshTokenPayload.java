package com.vtes.payload;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RefreshTokenPayload {
	@NotBlank
	private String refreshToken;

}
