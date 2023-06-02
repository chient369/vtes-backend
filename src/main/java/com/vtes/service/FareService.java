package com.vtes.service;

import java.text.ParseException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.vtes.entity.Fare;
import com.vtes.exception.VtesException;
import com.vtes.model.FareDTO;
import com.vtes.repository.FareRepo;

@Service
public class FareService {
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private FareRepo repo;

	public FareDTO saveFareRecord(FareDTO fareDTO) throws MethodArgumentNotValidException, ParseException {
		Fare fare = convertToFare(fareDTO);
		Fare savedFare = repo.save(fare);
		return convertFromFare(savedFare);

	}

	public void deleteFareRecord(Integer recordId) throws VtesException {
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

	private FareDTO convertFromFare(Fare fare) {
		FareDTO fareDTO = modelMapper.map(fare, FareDTO.class);
		return fareDTO;

	}
	
	private Fare convertToFare(FareDTO fareDTO) {
		return modelMapper.map(fareDTO, Fare.class);
		
	}
}
