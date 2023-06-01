package com.vtes.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.vtes.entity.Fare;
import com.vtes.exception.FareNotFoundException;
import com.vtes.model.FareDTO;
import com.vtes.repository.FareRepo;

@Service
public class FareService {

	@Autowired
	private FareRepo repo;

	public FareDTO saveFareRecord(FareDTO fareDTO) throws MethodArgumentNotValidException, ParseException {
		Fare savedFare = repo.save(fareDTO.convertToFare());
		return convertFromFare(savedFare);

	}

	public void deleteFareRecord(Integer recordId) throws FareNotFoundException {
		repo.deleteById(recordId);
	}
	
	public void deleteByUserId(Integer userId) {
		repo.deleteByUserId(userId);
	}
	
	public List<Fare> finByUserId(Integer userId) {
		return repo.finByUserId(userId);
	}

	public boolean isExistFare(Integer userId,Integer recordId) {
		return repo.findByIdAnhUserId(userId, recordId).isEmpty() ? false : true;
	}

	public FareDTO convertFromFare(Fare fare) {
		return FareDTO.builder()
				.id(fare.getId())
				.userId(fare.getUser().getId())
				.departure(fare.getDeparture())
				.visitLocation(fare.getVisitLocation())
				.destination(fare.getDestination())
				.fee(fare.getFee())
				.payMethod(fare.getPayMethod())
				.useCommuterPass(fare.getUseCommuterPass())
				.isRoundTrip(fare.getIsRoundTrip())
				.transportation(fare.getTransportation())
				.visitDate(fare.getVisitDate())
				.build();

	}

}
