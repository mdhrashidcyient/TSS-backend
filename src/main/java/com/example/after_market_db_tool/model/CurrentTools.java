package com.example.after_market_db_tool.model;

import java.util.Date;

public class CurrentTools {
	
	public String vendor;
	public Date date;
	public int total;
	
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vednor) {
		this.vendor = vednor;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}

}
