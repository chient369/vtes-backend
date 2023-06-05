package com.vtes.payload;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.vtes.model.CommuterPassDTO;

import lombok.Data;

@Data
public class UpdateUserPayload {
	@NotBlank
	@Size(min = 4, max = 64)
	private String fullName;

	@Size(max = 64)
	private String oldPassword;

	@Size(min = 8, max = 64)
	private String newPassword;

	@NotNull
	@Min(value = 1)
	@Max(value = 999)
	private int departmentId;

	private CommuterPassDTO commuterPass;

}
