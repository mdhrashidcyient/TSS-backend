package com.example.after_market_db_tool.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.after_market_db_tool.entity.UserEntity;
import com.example.after_market_db_tool.entity.VerificationToken;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationToken,Long>{

	Optional<VerificationToken> findByToken(String token);

}
