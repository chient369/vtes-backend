package com.vtes.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@JsonIgnore
	private List<User> user;
	
	@Column(name = "`CREATE_DT`")
	private Date createDt;

	@Column(name = "`UPDATE_DT`") 
	private Date updateDt;
	
	public Department(int departmentId) {
		this.id = departmentId;
	}

}
