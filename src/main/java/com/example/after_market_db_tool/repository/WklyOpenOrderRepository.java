package com.example.after_market_db_tool.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import com.example.after_market_db_tool.dto.WklyOpenOrderDto;
import com.example.after_market_db_tool.entity.CurrentWeekOpenOrder;
import com.example.after_market_db_tool.model.CurrentTools;
import com.example.after_market_db_tool.model.OpenOrders;

@Repository
public interface WklyOpenOrderRepository extends JpaRepository<CurrentWeekOpenOrder,Long> {
	
		
	 @Procedure(procedureName = "update_wkly_open_order_tbl")
     void callStoredProcedure();
	 
	 
	 List<CurrentWeekOpenOrder> findByCyientAnalyst(String cyientAnalyst);
	 Optional<CurrentWeekOpenOrder> findById(Long id);
	 
	 List<CurrentWeekOpenOrder> findByVendorCode(String vendorCode);
	 
	 @Query(value = "SELECT vendor_name as vendorName, sum(open_quantity) as openQuantity \r\n"
	 		+ "FROM public.current_week_open_order\r\n"
	 		+ "group by vendor_name\r\n"
	 		+ "ORDER BY vendor_name ASC",nativeQuery = true)
	 List<OpenOrder> getOpenOrdersList();
	 
	 @Query(value = "SELECT vendor_name as vendor ,statistics_date as date,  open_quantity as total\r\n"
	 		+ "FROM public.current_week_open_order\r\n"
	 		+ "where statistics_date <> '9999-09-09'\r\n"
	 		+ "ORDER BY vendor_name ASC",nativeQuery = true)
		 List<CurrentTool> getCurrentToolList();
	 
	 
	


}