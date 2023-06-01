package com.vtes.payload.response;

import java.util.List;
import com.vtes.model.CommuterPassDTO;
import com.vtes.model.FareDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private String fullName;

	private String email;

	private DepartmentResponse department;

	private CommuterPassDTO commuterPass;

	private List<FareDTO> fares;
}
