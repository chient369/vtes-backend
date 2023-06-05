package com.vtes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder("departmentId")
public class DepartmentDTO {
	@JsonProperty("departmentId")
	private Integer id;

	private String departmentName;
}
