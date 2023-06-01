package com.vtes.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.vtes.entity.Fare;

@Repository
public interface FareRepo extends JpaRepository<Fare, Integer> {

	@Query("select f from Fare f where f.user.id = ?1 and f.deleteFlag = 0 order by f.visitDate asc")
	List<Fare> finByUserId(Integer userId);
	
	void deleteById(Integer id);
	
	Fare save(Fare fare); 
	
	@Query("select f from Fare f where f.user.id = ?1 and f.id = ?2")
	Optional<Fare> findByIdAnhUserId(Integer userId, Integer recordId);
	
	@Query("update Fare f set f.deleteFlag = 1 where f.user.id = ?1")
	@Modifying
	@Transactional
	void deleteByUserId(Integer userId);
}
