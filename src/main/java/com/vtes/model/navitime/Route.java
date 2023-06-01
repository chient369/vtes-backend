package com.vtes.model.navitime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("data")
public class Route {
	private RouteSummary summary;
	private List<RouteSectionItem> sections;
}
