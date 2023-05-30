package com.vtes.service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vtes.model.navitime.CommuterPassRoute;
import com.vtes.model.navitime.Link;
import com.vtes.model.navitime.Route;
import com.vtes.model.navitime.Station;

import lombok.extern.slf4j.Slf4j;

/*
 * Author :chien.tranvan
 * Date: 2023/05/21
 * 
 * This get params from client and call third-part API then convert response data to navitime model
 * */

@Service
@Slf4j
public class TransportInfomationServiceImpl implements TransportInfomationService {
	private static final String STATION = "station";
	private static final String STATION_JA = "駅";
	private static final String ITEMS = "items";
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String MOVE = "move";
	private static final Integer RESULT_LIMIT = 100;
	private static final String PREFIX_KEY = "stations:";
	private static final Integer KEY_DURATION = 7;
	private static final String FLIGHT = "domestic_flight";

	@Autowired
	private TotalNaviApiConnect totalnavi;

	@Autowired
	private TransportApiConnect transport;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	public List<Route> searchRoutes(Map<String, Object> params) {
		List<Route> items = null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String formattedDateTime = sdf.format(new Date());

		// Put default requied field to call api
		params.put("start_time", formattedDateTime);

		// Filter routes use flight way
		params.put("unuse", FLIGHT);

		ResponseEntity<String> json = totalnavi.searchRoutes(params);
		String jsonString = json.getBody();

		try {
			JsonNode node = objectMapper.readTree(jsonString);
			JsonNode itemsNode = node.get(ITEMS);

			items = objectMapper.readValue(itemsNode.toString(), new TypeReference<List<Route>>() {
			});
		} catch (JsonProcessingException e) {
			log.error("Has error when call 3rd API");
		}

		return items;
	}

	// Call the api to a 3rd party and filter out the points that are train stations
	@Override
	public List<Station> searchStationsByWord(String stationName) {

		String jsonString = (String) redisTemplate.opsForValue().get(PREFIX_KEY + stationName);

		if (isNullObject(jsonString)) {
			jsonString = searchStationsFromNaviTime(stationName);
		}else {
			log.info("Get station data from Redis with key : {}", PREFIX_KEY + stationName);
		}
		return filterStations(jsonString);

	}

	private String searchStationsFromNaviTime(String stationName) {
		if (!keyRegexValidate(stationName)) {
			return null;
		}

		Map<String, Object> params = new HashMap<>();
		params.put("word", stationName);
		params.put("limit", RESULT_LIMIT);

		ResponseEntity<String> responseEntity = transport.getStationDetail(params);
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			String jsonString = responseEntity.getBody();
			redisTemplate.opsForValue().set(PREFIX_KEY + stationName, jsonString, Duration.ofDays(KEY_DURATION));
			log.info("Stored station detail with key: {}", PREFIX_KEY + stationName);
			return jsonString;
		}

		return null;
	}

	private List<Station> filterStations(String jsonString) {
		if (isNullObject(jsonString)) {
			return null;
		}
		;
		try {
			JsonNode node = objectMapper.readTree(jsonString);
			JsonNode itemsNode = node.get(ITEMS);
			List<Station> stations = objectMapper.readValue(itemsNode.toString(), new TypeReference<List<Station>>() {
			});

			return stations.stream().filter(s -> s.getTypes()
					.contains(STATION))
					.peek(s -> s.setName(s.getName() + STATION_JA))
					.collect(Collectors.toList());
		} catch (JsonProcessingException e) {
			log.debug("Error occurred while mapping to List<Station>");
		}

		return new ArrayList<>();
	}

	public List<CommuterPassRoute> searchCommuterPassDetail(Map<String, Object> params) {
		List<Route> routes = searchRoutes(params);
		return convertCommuterPass(routes);

	}

	// Get route details and convert to a commuter pass used for next request
	private List<CommuterPassRoute> convertCommuterPass(List<Route> routes) {
		List<CommuterPassRoute> cpDetails = routes.stream().map(route -> {
			CommuterPassRoute cpRoute = new CommuterPassRoute();
			cpRoute.setSummary(route.getSummary());
			cpRoute.setSections(route.getSections());

			List<String> cpLink = route.getSections().stream()
					.filter(sc -> MOVE.equals(sc.getType()) && sc.getTransport() != null)
					.flatMap(sc -> Optional.ofNullable(sc.getTransport().getLinks()).orElse(Collections.emptyList())
							.stream().map(Link::generateViaJson))
					.collect(Collectors.toList());
			cpRoute.setCommuterPassLink(cpLink);

			return cpRoute;
		}).collect(Collectors.toList());

		return cpDetails;
	}

	private boolean keyRegexValidate(String keyWord) {
		String hiraganaRegex = "^[ぁ-ん]{4,}$";
		String katakanaRegex = "^[ァ-ン]{4,}$";
		String kanjiRegex = "^[一-龯]{2,}$";

		return Pattern.matches(hiraganaRegex, keyWord) || Pattern.matches(katakanaRegex, keyWord)
				|| Pattern.matches(kanjiRegex, keyWord);
	}

	private boolean isNullObject(Object ob) {
		return ob == null ? true : false;
	}

}
