package com.vtes.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private String fullName;

	private String email;

	private DepartmentDTO department;

	private CommuterPassDTO commuterPass;

	private List<FareDTO> fares;
}
