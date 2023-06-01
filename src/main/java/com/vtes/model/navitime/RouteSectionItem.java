package com.vtes.model.navitime;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class RouteSectionItem {
	private String type;
	
	@Getter(onMethod_ = {@JsonGetter("stationCode")})
	@Setter(onMethod_ = {@JsonSetter("node_id")})
	private String nodeId;
	
	@Getter(onMethod_ = {@JsonGetter("stationName")})
	@Setter(onMethod_ = {@JsonSetter("name")})
	private String name;
	
	private RouteNodeNumber numbering;
	
	private Transport transport;
}
