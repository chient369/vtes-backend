package com.vtes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.vtes.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);

	Optional<User> findByVerifyCode(String verifyCode);
	
	@Modifying
	void deleteById(String userId);

	Boolean existsByEmail(String email);
	
	@Modifying
	User save(User user);
}
