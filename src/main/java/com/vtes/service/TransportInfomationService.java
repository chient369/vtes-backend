package com.vtes.service;

import java.util.List;
import java.util.Map;

import com.vtes.exception.NotFoundCommuterPassValid;
import com.vtes.model.navitime.CommuterPassRoute;
import com.vtes.model.navitime.Route;
import com.vtes.model.navitime.Station;
/*
 * @Author : chien.tranvan
 * Date : 2023/05/21
 * Define methods of obtaining 3rd party information
 * */
public interface TransportInfomationService {
	
	public List<Route> searchRoutes(Map<String, Object> params);
	
	public List<Station> searchStationsByWord(String stationName);
	
	public List<CommuterPassRoute> searchCommuterPassDetail(Map<String, Object> params) throws NotFoundCommuterPassValid;


}
