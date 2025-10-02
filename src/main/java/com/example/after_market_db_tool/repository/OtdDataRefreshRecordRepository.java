package com.example.after_market_db_tool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.after_market_db_tool.entity.OtdDataRefreshRecord;


@Repository
public interface OtdDataRefreshRecordRepository extends JpaRepository<OtdDataRefreshRecord,Long>{

	OtdDataRefreshRecord findByUserId(String userId);
	OtdDataRefreshRecord findTopByOrderByIdDesc();

}