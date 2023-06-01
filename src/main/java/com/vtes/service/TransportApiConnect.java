package com.vtes.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vtes.config.FeignClientConfig;

/*
 * Author :chien.tranvan
 * Date: 2023/05/21
 * 
 * Define connect to navitime api service from 3rdpart RapidAPI
 * transport endpoint use to search train of details, subway links, ...etc
 * */


@FeignClient(name ="navitime-transport",url="https://navitime-transport.p.rapidapi.com", configuration = FeignClientConfig.class)
public interface TransportApiConnect {
	@GetMapping("/transport_node")
	public ResponseEntity<String> getStationDetail(@RequestParam Map<String, Object> params);
}
