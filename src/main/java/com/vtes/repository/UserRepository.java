package com.vtes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vtes.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);
	
	Optional<User> findById(Integer userId);

	Optional<User> findByVerifyCode(String verifyCode);
	
	@Modifying
	void deleteById(String userId);

	@Query("select count(u) from User u where u.email = ?1 and u.status = 1 ")
	Long findActiveUserByEmail(String email);
	
	Boolean existsByEmail(String email);
	
	@Modifying
	User save(User user);
}
