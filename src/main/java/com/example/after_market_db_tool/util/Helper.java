package com.example.after_market_db_tool.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Helper {
	
	
	public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDate();
	}

}
