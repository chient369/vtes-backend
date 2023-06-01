package com.vtes.model.navitime;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Move {
	@Getter(onMethod_ = { @JsonGetter("transitCount") })
	@Setter(onMethod_ = { @JsonSetter("transit_count") })
	private int transitCount;

	@Getter(onMethod_ = { @JsonGetter("fare") })
	@Setter(onMethod_ = { @JsonSetter("fare") })
	@JsonSerialize(using = FareSerializer.class)
	private Map<String, Integer> fares;
}

class FareSerializer extends JsonSerializer<Map<String, Integer>> {

	@Override
	public void serialize(Map<String, Integer> value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException {
		gen.writeStartObject();
		for (Map.Entry<String, Integer> entry : value.entrySet()) {
			String key = entry.getKey();
			switch (key) {
			case "unit_0":
				key = "現金";
				break;
			case "unit_48":
				key = "IC";
				break;
			case "unit_112":
				key = "unit112";
				break;
			case "unit_113":
				key = "unit113";
				break;
			case "unit_114":
				key = "unit114";
				break;
			default:
				continue;
			}
			gen.writeNumberField(key, entry.getValue());

		}
		gen.writeEndObject();

	}
}
