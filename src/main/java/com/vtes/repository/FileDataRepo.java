package com.vtes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vtes.entity.FileData;

public interface FileDataRepo extends CrudRepository<FileData, Integer>{

	@Query("select f.fileName from FileData f where f.id=?1")
	String findFileNameById(Integer id);
	
	@Query("select f from FileData f where f.id = ?1 and f.user.id = ?2")
	Optional<FileData> findByIdAndUserId(Integer fileId, Integer userId);
	
	@Query("select f from FileData f where f.user.id = ?1")
	Optional<List<FileData>> findByUserId(Integer userId);
	
}
