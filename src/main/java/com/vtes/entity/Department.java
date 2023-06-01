package com.vtes.entity;

import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tbl_department")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;
	
	@Column(name = "`DEPARTMENT_NAME`", nullable = false)
	private String departmentName;
	
	@OneToMany(mappedBy = "department")
	private List<User> user;
	
	@Column(name = "`CREATE_DT`")
	private Instant createDt;

	@Column(name = "`UPDATE_DT`") 
	private Instant updateDt;
}
