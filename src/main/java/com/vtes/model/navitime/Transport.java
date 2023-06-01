package com.vtes.model.navitime;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transport {
	@Getter(onMethod_ = {@JsonGetter("lineColor")} )
	@Setter(onMethod_ = {@JsonSetter("color")} )
	private String lineColor;
	
	@Setter(onMethod_ = {@JsonSetter("name")} )
	@Getter(onMethod_ = {@JsonGetter("lineName")})
	private String name;
	private List<Link> links;
}
