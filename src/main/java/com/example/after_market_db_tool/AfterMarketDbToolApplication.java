package com.example.after_market_db_tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.after_market_db_tool.entity.AfterMarketVendors;
import com.example.after_market_db_tool.repository.AfterMarketVendorsRepository;
import com.example.after_market_db_tool.service.LoadDataToPostGres;

@SpringBootApplication
public class AfterMarketDbToolApplication {
	
	
	@Autowired
	LoadDataToPostGres obj;

	public static void main(String[] args) {
		SpringApplication.run(AfterMarketDbToolApplication.class, args);
		
//		final AfterMarketVendorsRepository afterMarketVendorsRepository;
//		
//		LoadDataToPostGres obj = new LoadDataToPostGres(afterMarketVendorsRepository);
//		obj.uploadDataToPostGres();
	}

}
