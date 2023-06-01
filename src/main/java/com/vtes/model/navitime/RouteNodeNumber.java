package com.vtes.model.navitime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RouteNodeNumber {
	private List<NodeNumber> departure;
	private List<NodeNumber> arrival;
}
