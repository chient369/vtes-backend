package com.vtes.model.navitime;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkNodeItem {
	@Setter(onMethod_ = {@JsonSetter("name")} )
	@Getter(onMethod_ = {@JsonGetter("stationName")})
	private String name;
	@Setter(onMethod_ = {@JsonSetter("id")} )
	@Getter(onMethod_ = {@JsonGetter("stationCode")})
	private String id;

}
