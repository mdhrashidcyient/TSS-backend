package com.example.after_market_db_tool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.after_market_db_tool.entity.WipDataRefreshRecord;
import com.example.after_market_db_tool.entity.Otp;


@Repository
public interface WipDataRefreshRecordRepository extends JpaRepository<WipDataRefreshRecord,Long>{

	WipDataRefreshRecord findByUserId(String userId);
	WipDataRefreshRecord findTopByOrderByIdDesc();

}