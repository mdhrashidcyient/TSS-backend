package com.example.after_market_db_tool.model;

import java.time.LocalDate;
import java.util.Date;

public class SupplierScoreCard {
	
	public long count;
	public String vendorName;
	public LocalDate grDate;
	
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public LocalDate getGrDate() {
		return grDate;
	}
	public void setGrDate(LocalDate grDate) {
		this.grDate = grDate;
	}
}
