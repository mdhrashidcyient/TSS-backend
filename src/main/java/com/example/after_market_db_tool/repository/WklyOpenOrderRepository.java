package com.example.after_market_db_tool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import com.example.after_market_db_tool.dto.WklyOpenOrderDto;
import com.example.after_market_db_tool.entity.CurrentWeekOpenOrder;

@Repository
public interface WklyOpenOrderRepository extends JpaRepository<CurrentWeekOpenOrder,Long> {
	
		
	 @Procedure(procedureName = "update_wkly_open_order_tbl")
     void callStoredProcedure();
	 
	 
	 List<CurrentWeekOpenOrder> findByCyientAnalyst(String cyientAnalyst);
	 Optional<CurrentWeekOpenOrder> findById(Long id);
	 
	 List<CurrentWeekOpenOrder> findByVendorCode(String vendorCode);
	


}