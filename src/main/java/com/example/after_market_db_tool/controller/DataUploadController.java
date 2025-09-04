package com.example.after_market_db_tool.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.poi.EncryptedDocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.after_market_db_tool.entity.CurrentWeekOpenOrder;
import com.example.after_market_db_tool.entity.OtdData;
import com.example.after_market_db_tool.entity.UserEntity;
import com.example.after_market_db_tool.entity.VerificationToken;
import com.example.after_market_db_tool.model.CreateUserRequest;
import com.example.after_market_db_tool.repository.OtdDataRepository;
import com.example.after_market_db_tool.repository.UserRepository;
import com.example.after_market_db_tool.repository.VerificationRepository;
import com.example.after_market_db_tool.repository.WklyOpenOrderRepository;
import com.example.after_market_db_tool.security.SecurityUtils;
import com.example.after_market_db_tool.service.AfterMarketDBToolService;
import com.example.after_market_db_tool.service.JwtService;
import com.example.after_market_db_tool.service.LoadDataToPostGres;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/aftermarket")
@CrossOrigin(origins = "http://localhost:51648")
public class DataUploadController {

	@Autowired
	LoadDataToPostGres loadDataToPostGres;

	@Autowired
	AfterMarketDBToolService afterMarketDBToolService;

	@Autowired
	WklyOpenOrderRepository wklyOpenOrderRepository;

	@Autowired
	UserRepository userRepository;	


	@Autowired
	private JwtService jwtService;

	@Autowired
	private VerificationRepository tokenRepo;
	
	@Autowired
	private OtdDataRepository otdDataRepository;
	
	@Autowired private JavaMailSender mailSender;

	@GetMapping("/uploadAfterMarketVendorData")
	public String uploadAfterVendormarketData() {
		loadDataToPostGres.uploadDataToPostGres();
		return "OK";
	}

	@GetMapping("/uploadWklyOpenOrderData")
	public String uploadWklyOrderData() throws InvalidFormatException, EncryptedDocumentException,
			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException, SQLException {
		afterMarketDBToolService.refreshDBWithWIPData("C:/Users/mr69509/Downloads/Weekly Data.xlsx");
		afterMarketDBToolService.saveDataIntoWklyOpenOrder();
		return "OK";
	}
	
	@GetMapping("/uploadWklyOtdData")
	public String uploadWklyOtdData() throws InvalidFormatException, EncryptedDocumentException,
			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException, SQLException {
		afterMarketDBToolService.uploadOtdData("C:/Users/mr69509/Downloads/BW_OTD_Data.xlsx");
		
		return "Successfully uploaded OTD data";
	}

	@GetMapping("/uploadPrevOpenOrderData")
	public String uploadPrevOpenOrderData() throws InvalidFormatException, EncryptedDocumentException,
			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException {
		afterMarketDBToolService.uploadPrevOpenOrderData("C:/Users/mr69509/Downloads/Prev-Open-Order-v6.xlsx");
		return "Open Order Data Copied";
	}
	
	@GetMapping("/uploadPrevWeekAnalystData")
	public String uploadPrevWeekAnalystData() throws InvalidFormatException, EncryptedDocumentException,
			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException {
		afterMarketDBToolService.uploadPrevWeekAnalystData("C:/Users/mr69509/Downloads/Analystcomments.xlsx");
		return "Open Order Data Copied";
	}

	@GetMapping("/by-analyst")
	public ResponseEntity<List<CurrentWeekOpenOrder>> getWklyOpenOrderByCyientAnalyst(@RequestParam String analyst) {
		List<CurrentWeekOpenOrder> orders = afterMarketDBToolService.getOrdersByAnalyst(analyst);
		if (orders.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(orders);
	}
	
	@GetMapping("/by-supplier")
	public ResponseEntity<List<CurrentWeekOpenOrder>> getWklyOpenOrderByVendorCode(@RequestParam String vendorCode) {
		List<CurrentWeekOpenOrder> orders = afterMarketDBToolService.getOrdersByVendorCode(vendorCode);
		if (orders.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(orders);
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<CurrentWeekOpenOrder>> getAllWklyOpenOrderData() {
		List<CurrentWeekOpenOrder> orders = wklyOpenOrderRepository.findAll();
		if (orders.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(orders);
	}
	
	@GetMapping("/otdReport")
	public ResponseEntity<List<OtdData>> getAllOtdData() {
		List<OtdData> orders = otdDataRepository.findAll();
		if (orders.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(orders);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CurrentWeekOpenOrder> updateOrder(@PathVariable Long id,
			@RequestBody CurrentWeekOpenOrder updatedOrder) {
		return wklyOpenOrderRepository.findById(id).map(order -> {
			order.setLastWeekAnalystComments(updatedOrder.getLastWeekAnalystComments());
			order.setCurrentWeekAnalystComments(updatedOrder.getCurrentWeekAnalystComments());
			order.setStatus(updatedOrder.getStatus());

			wklyOpenOrderRepository.save(order);
			return ResponseEntity.ok(order);
		}).orElse(ResponseEntity.notFound().build());
	}





}
