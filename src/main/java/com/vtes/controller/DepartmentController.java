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
import com.vtes.model.DepartmentDTO;
import com.vtes.model.ResponseData;
import com.vtes.model.ResponseData.ResponseType;
import com.vtes.service.DepartmentService;

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

		List<DepartmentDTO> departmentResponses = modelMapper.map(departments,
				new TypeToken<List<DepartmentDTO>>() {
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
