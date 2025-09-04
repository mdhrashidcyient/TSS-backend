package com.example.after_market_db_tool.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="AnalystComments")
//@NamedStoredProcedureQuery(name = "updatewklyOpenOrder",
//procedureName = "UPDATE_WKLY_OPEN_ORDER"
//)


public class PrevWeekAnalystReport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;	
	 private String poNumber;	 
	 private String currentWeekAnalystComments;
	 private String poLineItem;
	 private String lastWeekAnalystComments;
	 
	 public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	public String getPoLineItem() {
		return poLineItem;
	}
	public void setPoLineItem(String poLineItem) {
		this.poLineItem = poLineItem;
	}
	public String getLastWeekAnalystComments() {
		return lastWeekAnalystComments;
	}
	public void setLastWeekAnalystComments(String lastWeekAnalystComments) {
		this.lastWeekAnalystComments = lastWeekAnalystComments;
	}
		 
	 public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	
    public String getCurrentWeekAnalystComments() {
		return currentWeekAnalystComments;
	}
	public void setCurrentWeekAnalystComments(String currentWeekAnalystComments) {
		this.currentWeekAnalystComments = currentWeekAnalystComments;
	}	 
     
}


