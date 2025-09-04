package com.example.after_market_db_tool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.example.after_market_db_tool.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>{
	
	Optional<UserEntity> findByEmail(String email);

	boolean existsByEmail(String email);


}
