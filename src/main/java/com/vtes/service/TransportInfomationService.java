package com.vtes.service;

import java.util.List;
import java.util.Map;

import com.vtes.exception.NavitimeConnectException;
import com.vtes.exception.NotFoundException;
import com.vtes.exception.VtesException;
import com.vtes.model.navitime.CommuterPassRoute;
import com.vtes.model.navitime.Route;
import com.vtes.model.navitime.Station;
/*
 * @Author : chien.tranvan
 * Date : 2023/05/21
 * Define methods of obtaining 3rd party information
 * */
public interface TransportInfomationService {
	
	public List<Route> searchRoutes(Map<String, Object> params) throws NavitimeConnectException;
	
	public List<Station> searchStationsByWord(String stationName) throws NavitimeConnectException, VtesException;
	
	public List<CommuterPassRoute> searchCommuterPassDetail(Map<String, Object> params) throws NotFoundException, NavitimeConnectException;


}
