package com.vtes.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vtes.entity.Department;
import com.vtes.payload.response.DepartmentResponse;
import com.vtes.payload.response.ResponseData;
import com.vtes.payload.response.ResponseData.ResponseType;
import com.vtes.service.DepartmentService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping()
	public ResponseEntity<?> getAllDepartments() {
		List<Department> departments = departmentService.getAllDepartments();

		List<DepartmentResponse> departmentResponses = modelMapper.map(departments,
				new TypeToken<List<DepartmentResponse>>() {
				}.getType());
		return ResponseEntity.ok()
				.body(ResponseData.builder()
						.type(ResponseType.INFO)
						.code("")
						.message("Success")
						.data(departmentResponses)
						.build());
	}

}
