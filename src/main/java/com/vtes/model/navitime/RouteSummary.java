package com.vtes.model.navitime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteSummary {
	private Integer no;
	private RouteSummaryNode start;
	private RouteSummaryNode goal;
	private Move move;

}
