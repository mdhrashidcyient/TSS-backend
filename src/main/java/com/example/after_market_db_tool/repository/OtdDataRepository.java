package com.example.after_market_db_tool.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.after_market_db_tool.entity.OtdData;


@Repository
public interface OtdDataRepository extends JpaRepository<OtdData,Long> {	 
	 List<OtdData> findAll();

}