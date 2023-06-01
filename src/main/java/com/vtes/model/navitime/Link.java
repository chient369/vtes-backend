package com.vtes.model.navitime;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {
	@Setter(onMethod_ = {@JsonSetter("id")} )
	@Getter(onMethod_ = {@JsonGetter("lineId")})
	private String id;
	
	@Setter(onMethod_ = {@JsonSetter("name")} )
	@Getter(onMethod_ = {@JsonGetter("lineName")})
	private String name;

	private String direction;
	private LinkNodeItem from;
	private LinkNodeItem to;
	
	
	//Use to generate via commuter pass link
	public String generateViaJson() {
		Map<String, String> link = new HashMap<>();
		link.put("start", from.getId());
		link.put("goal", to.getId());
		link.put("direction", direction);
		link.put("link", id);

		try {
			String json = new ObjectMapper().writeValueAsString(link);
			return json;
		} catch (JsonProcessingException e) {
		}
		return null;
	}

}
