package com.example.after_market_db_tool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import com.example.after_market_db_tool.entity.WklyOpenOrderTemp;

@Repository
public interface AfterMarketDBToolRepository extends JpaRepository<WklyOpenOrderTemp,Long> {
	
	

}
