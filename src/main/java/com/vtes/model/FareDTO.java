package com.vtes.model;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FareDTO {

	@JsonProperty("recordId")
	private Integer id;

	@JsonIgnore
	private Integer userId;

	@NotBlank
	private String visitLocation;

	@NotBlank
	private String departure;

	@NotBlank
	private String destination;

	@NotNull
	private String payMethod;

	@NotNull
	private Boolean useCommuterPass;

	@NotNull
	private Boolean isRoundTrip;

	@NotNull
	private Integer fee;

	@NotNull
	private String transportation;

	@NotNull
	@JsonFormat(pattern = "yyyy/MM/dd")
	private Date visitDate;
	
	@NotNull
	private Date createDate = new Date();
	
	@NotNull
	private Date updateDate = new Date();
	
	@NotNull
	@JsonIgnore
	private boolean deleteFlag = false;
}

