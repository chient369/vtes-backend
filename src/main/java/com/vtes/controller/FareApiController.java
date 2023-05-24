package com.vtes.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vtes.exception.FareNotFoundException;
import com.vtes.model.FareDTO;
import com.vtes.payload.response.ResponseData;
import com.vtes.payload.response.ResponseData.ResponseType;
import com.vtes.sercurity.services.UserDetailsImpl;
import com.vtes.service.FareService;

@RestController
@RequestMapping("/api/v1")
public class FareApiController {

	@Autowired
	private FareService fareService;

	@PostMapping("/fares")
	public ResponseEntity<?> saveFareRecord(@Valid @RequestBody FareDTO fareDTO, HttpServletRequest request) throws ParseException, MethodArgumentNotValidException {
		fareDTO.setUserId(getAuthenticatedUserId());
		FareDTO savedFare = fareService.saveFareRecord(fareDTO);
		
		return ResponseEntity.ok()
				.body(ResponseData.builder()
						.code("200")
						.message("Fare detail saved")
						.type(ResponseType.INFO)
						.data(savedFare)
						.build()
						);
				
	}
	@DeleteMapping(value = "/fares", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteFareRecordById(
			@RequestParam(name = "recordId",required = true) Integer recordId) throws FareNotFoundException {
		Integer userId = getAuthenticatedUserId();

		if(!fareService.isExistFare(userId,recordId))
			throw new FareNotFoundException("Fare ID is not found");
		
		fareService.deleteFareRecord(recordId);
		return ResponseEntity.ok()
				.body(ResponseData.builder()
					.code("200")
					.message("Deleted")
					.type(ResponseType.INFO)
					.build());
						

		}
	
	private Integer getAuthenticatedUserId() {
		// Get authenticated user from security context
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
		return userDetails.getId();
	}
	
	
}
