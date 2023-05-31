package com.vtes.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vtes.entity.CommuterPass;
import com.vtes.exception.CommuterPassNotFound;
import com.vtes.exception.NotFoundCommuterPassValid;
import com.vtes.exception.ParameterInvalidException;
import com.vtes.model.navitime.CommuterPassRoute;
import com.vtes.model.navitime.Node;
import com.vtes.model.navitime.Route;
import com.vtes.model.navitime.Station;
import com.vtes.payload.response.ResponseData;
import com.vtes.payload.response.ResponseData.ResponseType;
import com.vtes.repository.CommuterPassRepo;
import com.vtes.security.services.UserDetailsImpl;
import com.vtes.service.TransportInfomationServiceImpl;

import lombok.extern.slf4j.Slf4j;

/*
 * Author : Chien@vti
 * Date : 2023/05/20
 * - Receive requests and return results to users when looking up routes, station details and ticket
 * */

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TransportAPIController {

	@Autowired
	private TransportInfomationServiceImpl transportService;

	@Autowired
	private CommuterPassRepo repo;

	@GetMapping(value = "/routes")
	public ResponseEntity<?> getRouteDetails(@RequestParam(name = "start", required = true) String start,
			@RequestParam(name = "goal", required = true) String goal,
			@RequestParam(name = "viaCode", required = false) String[] via,
			@RequestParam(name = "commuterPass", required = false) boolean cpActive)
			throws ParameterInvalidException, CommuterPassNotFound {

		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("goal", goal);
		params.put("via", viaNodeJson(via));

		Integer userId = getAuthenticatedUserId();
		/*Handling the case of using commuter pass:
		*getcommuter pass information from DB and set to params
		**/
		if (cpActive) {
			Optional<CommuterPass> commuterPassOptional = repo.findByUserId(userId);
			if (!commuterPassOptional.isPresent()) {
				log.warn("Not found commuter pass with user id {}", userId);
				throw new CommuterPassNotFound(userId);
			}
			CommuterPass commuterPass = commuterPassOptional.get();
			params.put("commuter_pass", commuterPass.getViaDetail());
		}

		List<Route> routes = transportService.searchRoutes(params);

		return ResponseEntity.ok()
				.body(ResponseData.builder().code("").message("Success").type(ResponseType.INFO).data(routes).build());

	}

	@GetMapping("/stations")
	public ResponseEntity<?> getStationsByWord(@RequestParam(name = "stationName", required = true) String word)
			throws ParameterInvalidException {
		
		List<Station> stations = transportService.searchStationsByWord(word);
		return ResponseEntity.ok().body(ResponseData.builder()
										.code("")
										.message("Success")
										.type(ResponseType.INFO)
										.data(stations)
										.build());
	}

	@GetMapping("/cp-routes")
	public ResponseEntity<?> getCommuterPass(@RequestParam(name = "start", required = true) String start,
			@RequestParam(name = "goal", required = true) String goal) throws ParameterInvalidException, NotFoundCommuterPassValid {

		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("goal", goal);

		List<CommuterPassRoute> cpRoutes = transportService.searchCommuterPassDetail(params);

		return ResponseEntity.ok().body(ResponseData.builder()
									.code("")
									.message("Success")
									.type(ResponseType.INFO)
									.data(cpRoutes)
									.build());

	}

	
	private Integer getAuthenticatedUserId() {
		// Get authenticated user from security context
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
		return userDetails.getId();
	}
	
	private String viaNodeJson(String[] vias) {
		if (vias == null)
			return null;
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < vias.length; i++) {
			nodes.add(new Node(vias[i]));
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(nodes);
		} catch (JsonProcessingException e) {
		}
		return null;
	}
}
