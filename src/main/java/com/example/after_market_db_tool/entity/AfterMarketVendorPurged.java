package com.example.after_market_db_tool.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="AfterMarketVendorsPurged")


public class AfterMarketVendorPurged {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;	
	 private String purgedSupplierCode;
	 private String purgedSupplier;
	 
}
