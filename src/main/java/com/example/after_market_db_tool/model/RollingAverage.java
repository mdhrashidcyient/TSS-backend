package com.example.after_market_db_tool.model;

public class RollingAverage {
	public String vendorName;
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public double percentage;
	
	  @Override
	    public String toString() {
	        return vendorName + " (" + percentage + ")";
	    }
}
