package com.example.after_market_db_tool.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import com.example.after_market_db_tool.entity.CurrentWeekOpenOrder;
import com.example.after_market_db_tool.entity.OtdData;
import com.example.after_market_db_tool.entity.UserEntity;
import com.example.after_market_db_tool.entity.VerificationToken;
import com.example.after_market_db_tool.model.CreateUserRequest;
import com.example.after_market_db_tool.model.CurrentTools;
import com.example.after_market_db_tool.model.OpenOrders;
import com.example.after_market_db_tool.model.RollingAverage;
import com.example.after_market_db_tool.model.SupplierScoreCard;
import com.example.after_market_db_tool.model.SupplierScoreCardModel;
import com.example.after_market_db_tool.repository.CurrentTool;
import com.example.after_market_db_tool.repository.OpenOrder;
import com.example.after_market_db_tool.repository.OtdDataRepository;
import com.example.after_market_db_tool.repository.UserRepository;
import com.example.after_market_db_tool.repository.VerificationRepository;
import com.example.after_market_db_tool.repository.WklyOpenOrderRepository;
import com.example.after_market_db_tool.security.SecurityUtils;
import com.example.after_market_db_tool.service.AfterMarketDBToolService;
import com.example.after_market_db_tool.service.JwtService;
import com.example.after_market_db_tool.service.LoadDataToPostGres;
import com.example.after_market_db_tool.util.Helper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/aftermarket")
@CrossOrigin(origins = "http://tooling.cyient.com/")
//@CrossOrigin(origins = "*", allowedHeaders = "*")

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

	@Autowired
	private JavaMailSender mailSender;

	@PostMapping("/uploadAfterMarketVendorData")
	public ResponseEntity<String> uploadAfterVendormarketData(@RequestParam("file") MultipartFile file)
			throws InvalidFormatException, EncryptedDocumentException,
			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException, SQLException {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
		}

		try (InputStream is = file.getInputStream()) {
			loadDataToPostGres.uploadDataToPostGres(file);
			return new ResponseEntity<>("File uploaded and processed successfully!", HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to process the file: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

//	@GetMapping("/uploadWklyOpenOrderData")
//	public String uploadWklyOrderData() throws InvalidFormatException, EncryptedDocumentException,
//			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException, SQLException {
//		afterMarketDBToolService.refreshDBWithWIPData("C:/Users/mr69509/Downloads/Weekly Data.xlsx");
//		afterMarketDBToolService.saveDataIntoWklyOpenOrder();
//		return "OK";
//	}

	@PostMapping("/uploadWklyOpenOrderData")
	public ResponseEntity<String> uploadWklyOpenOrderData(@RequestParam("file") MultipartFile file)
			throws InvalidFormatException, EncryptedDocumentException,
			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException, SQLException {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
		}

		try (InputStream is = file.getInputStream()) {

			afterMarketDBToolService.refreshDBWithWIPData(file.getInputStream());
			afterMarketDBToolService.saveDataIntoWklyOpenOrder();
			return new ResponseEntity<>("File uploaded and processed successfully!", HttpStatus.OK);

		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to process the file: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/uploadWklyOtdData")
	public ResponseEntity<String> uploadWklyOtdData(@RequestParam("file") MultipartFile file)
			throws InvalidFormatException, EncryptedDocumentException,
			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException, SQLException {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
		}

		try (InputStream is = file.getInputStream()) {

			afterMarketDBToolService.uploadOtdData(file.getInputStream());
			return new ResponseEntity<>("File uploaded and processed successfully!", HttpStatus.OK);

		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to process the file: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@GetMapping("/uploadWklyOtdData")
//	public String uploadWklyOtdData() throws InvalidFormatException, EncryptedDocumentException,
//			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException, SQLException {
//		afterMarketDBToolService.uploadOtdData("C:/Users/mr69509/Downloads/BW_OTD_Data.xlsx");
//
//		return "Successfully uploaded OTD data";
//	}

//	@GetMapping("/uploadPrevOpenOrderData")
//	public String uploadPrevOpenOrderData() throws InvalidFormatException, EncryptedDocumentException,
//			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException {
//		afterMarketDBToolService.uploadPrevOpenOrderData("C:/Users/mr69509/Downloads/Prev-Open-Order-v6.xlsx");
//		return "Open Order Data Copied";
//	}

	@PostMapping("/uploadPrevOpenOrderData")
	public ResponseEntity<String> uploadPrevOpenOrderData(@RequestParam("file") MultipartFile file)
			throws InvalidFormatException, EncryptedDocumentException,
			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException, SQLException {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
		}

		try (InputStream is = file.getInputStream()) {

			afterMarketDBToolService.uploadPrevOpenOrderData(file.getInputStream());
			return new ResponseEntity<>("File uploaded and processed successfully!", HttpStatus.OK);

		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to process the file: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@GetMapping("/uploadPrevWeekAnalystData")
//	public String uploadPrevWeekAnalystData() throws InvalidFormatException, EncryptedDocumentException,
//			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException {
//		afterMarketDBToolService.uploadPrevWeekAnalystData("C:/Users/mr69509/Downloads/Analystcomments.xlsx");
//		return "Open Order Data Copied";
//	}

	@PostMapping("/uploadPrevWeekAnalystData")
	public ResponseEntity<String> uploadPrevWeekAnalystData(@RequestParam("file") MultipartFile file)
			throws InvalidFormatException, EncryptedDocumentException,
			org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException, ParseException, SQLException {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
		}

		try (InputStream is = file.getInputStream()) {

			afterMarketDBToolService.uploadPrevWeekAnalystData(file.getInputStream());
			return new ResponseEntity<>("File uploaded and processed successfully!", HttpStatus.OK);

		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to process the file: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
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

	@GetMapping("/download-afterMarketOtdReport")
	public ResponseEntity<ByteArrayResource> downloadAfterMarketOtdReport()
			throws IOException, EncryptedDocumentException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
		// HSSFWorkbook workbook = new HSSFWorkbook();
		// Populate your workbook with data here

		List<OtdData> list = otdDataRepository.findAll();
		List<SupplierScoreCard> ssc = new ArrayList<SupplierScoreCard>();
		Helper helper = new Helper();

		FileInputStream inputStream = new FileInputStream(
				"C:/Users/mr69509/Downloads/After Market On-Time Delivery 2025.xlsx");
		Workbook workbook = WorkbookFactory.create(inputStream);

		CreationHelper createHelper = workbook.getCreationHelper();
		CellStyle cellStyle = workbook.createCellStyle();

		// set the current month in supplier performance chart sheet
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		Sheet spSheet = workbook.getSheetAt(1);
		Row row = spSheet.getRow(spSheet.getFirstRowNum());
		Cell cell = row.getCell(30);
		cell.setCellValue(month);

		// Option 1: Using a custom format string
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
		Sheet sheet = workbook.getSheetAt(4);

		Sheet sscSheet = workbook.getSheetAt(2);

		int firstRowNum = sheet.getFirstRowNum();
		int i = 1;
		for (OtdData otdData : list) {
			SupplierScoreCard obj = new SupplierScoreCard();
			Row newRow = sheet.createRow(firstRowNum + i);
			Cell cell1 = newRow.createCell(0);
			cell1.setCellValue(otdData.getVendorCode());

			Cell cell2 = newRow.createCell(1);
			cell2.setCellValue(otdData.getVendorName());
			obj.setVendorName(otdData.getVendorName());

			Cell cell3 = newRow.createCell(2);
			cell3.setCellValue(otdData.getPlant());

			Cell cell4 = newRow.createCell(3);
			cell4.setCellValue(otdData.getPlant_());

			Cell cell5 = newRow.createCell(4);
			cell5.setCellValue(otdData.getPurchGroup());

			Cell cell6 = newRow.createCell(5);
			cell6.setCellValue(otdData.getBuyerName());

			Cell cell7 = newRow.createCell(6);
			cell7.setCellValue(otdData.getRegion());

			Cell cell8 = newRow.createCell(7);
			cell8.setCellValue(otdData.getSize());

			Cell cell9 = newRow.createCell(8);
			cell9.setCellValue(otdData.getSocio());

			Cell cell10 = newRow.createCell(9);
			cell10.setCellValue(otdData.getPoNumber());

			Cell cell11 = newRow.createCell(10);
			cell11.setCellValue(otdData.getItem());

			Cell cell12 = newRow.createCell(11);
			cell12.setCellValue(otdData.getCompletionCode());

			Cell cell13 = newRow.createCell(12);
			cell13.setCellValue(otdData.getAcctCat());

			Cell cell14 = newRow.createCell(13);
			cell14.setCellValue(otdData.getItemCat());

			Cell cell15 = newRow.createCell(14);
			cell15.setCellValue(otdData.getMaterial());

			Cell cell16 = newRow.createCell(15);
			cell16.setCellValue(otdData.getMaterialType());

			Cell cell17 = newRow.createCell(16);
			cell17.setCellValue(otdData.getDescription());

			Cell cell18 = newRow.createCell(17);
			cell18.setCellValue(otdData.getDocumentVersion());

			Cell cell19 = newRow.createCell(18);
			cell19.setCellValue(otdData.getWbsElement());

			Cell cell20 = newRow.createCell(19);
			cell20.setCellValue(otdData.getContact());

			Cell cell21 = newRow.createCell(20);
			cell21.setCellValue(otdData.getDeliveryDate());
			cell21.setCellStyle(cellStyle);

			Cell cell22 = newRow.createCell(21);
			cell22.setCellValue(otdData.getGrDate());
			cell22.setCellStyle(cellStyle);
			obj.setGrDate(helper.convertToLocalDateViaInstant(otdData.getGrDate()));

			Cell cell23 = newRow.createCell(22);
			cell23.setCellValue(otdData.getMaterialGroup());

			Cell cell24 = newRow.createCell(23);
			cell24.setCellValue(otdData.getMaterialDescription());

			Cell cell25 = newRow.createCell(24);
			cell25.setCellValue(otdData.getItemQuantity());

			Cell cell26 = newRow.createCell(25);
			cell26.setCellValue(otdData.getItemValue());

			LocalDate deliveryDate = otdData.getDeliveryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			LocalDate grDate = otdData.getGrDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			long numberOfDaysDifference = ChronoUnit.DAYS.between(grDate, deliveryDate.plusDays(3));
			obj.setCount(numberOfDaysDifference);

			Cell cell27 = newRow.createCell(26);
			cell27.setCellValue(numberOfDaysDifference);

			Cell cell28 = newRow.createCell(27);
			cell28.setCellValue(otdData.getItemQuantity() * otdData.getItemValue());
			ssc.add(obj);

			i++;

		}

		// getting value for Supplier score card sheet
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 12, 31);

		LocalDate today = LocalDate.now();

		// Get the first day of the current month, last year
		LocalDate firstDayLastYear = LocalDate.of(today.getYear() - 1, // last year
				today.getMonth(), // current month
				1 // first day of month
		);

		LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

		Map<String, Long> ontime = ssc.stream().filter(
				obj -> !obj.getGrDate().isBefore(startDate) && !obj.getGrDate().isAfter(endDate) && obj.getCount() >= 0)
				.collect(Collectors.groupingBy(SupplierScoreCard::getVendorName, Collectors.counting()));

		Map<String, Long> late = ssc.stream().filter(
				obj -> !obj.getGrDate().isBefore(startDate) && !obj.getGrDate().isAfter(endDate) && obj.getCount() <= 0)
				.collect(Collectors.groupingBy(SupplierScoreCard::getVendorName, Collectors.counting()));

		Map<String, Long> tmraOntime = ssc.stream()
				.filter(obj -> !obj.getGrDate().isBefore(firstDayLastYear) && !obj.getGrDate().isAfter(lastDayOfMonth)
						&& obj.getCount() >= 0)
				.collect(Collectors.groupingBy(SupplierScoreCard::getVendorName, Collectors.counting()));

		Map<String, Long> total = ssc.stream()
				.filter(obj -> !obj.getGrDate().isBefore(firstDayLastYear) && !obj.getGrDate().isAfter(lastDayOfMonth))
				.collect(Collectors.groupingBy(SupplierScoreCard::getVendorName, Collectors.counting()));

		List<SupplierScoreCardModel> sscList = new ArrayList<SupplierScoreCardModel>();

		for (i = 2; i < sscSheet.getLastRowNum(); i++) {

			if (sscSheet.getRow(i).getCell(0) != null) {
				System.out.println(sscSheet.getRow(i).getCell(0).getStringCellValue());
				Long onTimeValue = ontime.get(sscSheet.getRow(i).getCell(0).getStringCellValue());
				Long lateValue = late.get(sscSheet.getRow(i).getCell(0).getStringCellValue());
				// column 77
				Long tmraOneTineValue = tmraOntime.get(sscSheet.getRow(i).getCell(0).getStringCellValue());
				Long tmraTotal = total.get(sscSheet.getRow(i).getCell(0).getStringCellValue());

				// Creating obj for copy suppler score card to weekly supplier score card
				SupplierScoreCardModel obj = new SupplierScoreCardModel();
				obj.setVendorName(sscSheet.getRow(i).getCell(0).getStringCellValue());
				obj.setCell_1(sscSheet.getRow(i).getCell(1).getStringCellValue());
				obj.setCell_2(sscSheet.getRow(i).getCell(2).getStringCellValue());
				obj.setCell_3(sscSheet.getRow(i).getCell(3).getStringCellValue());
				obj.setCell_4(sscSheet.getRow(i).getCell(4).getStringCellValue());
				obj.setCell_5(sscSheet.getRow(i).getCell(5).getStringCellValue());
				obj.setCell_6(sscSheet.getRow(i).getCell(6).getStringCellValue());
				obj.setCell_7(sscSheet.getRow(i).getCell(7).getStringCellValue());
				obj.setCell_8(sscSheet.getRow(i).getCell(8).getStringCellValue());
				obj.setCell_9(sscSheet.getRow(i).getCell(9).getStringCellValue());
				obj.setCell_10(sscSheet.getRow(i).getCell(10).getStringCellValue());
				obj.setCell_11(sscSheet.getRow(i).getCell(11).getStringCellValue());
				obj.setCell_12(sscSheet.getRow(i).getCell(12).getStringCellValue());
				obj.setCell_13(sscSheet.getRow(i).getCell(13).getStringCellValue());
				obj.setCell_14(sscSheet.getRow(i).getCell(14).getStringCellValue());
				obj.setCell_15(sscSheet.getRow(i).getCell(15).getStringCellValue());
				obj.setCell_16(sscSheet.getRow(i).getCell(16).getStringCellValue());
				obj.setCell_17(sscSheet.getRow(i).getCell(17).getStringCellValue());
				obj.setCell_18(sscSheet.getRow(i).getCell(18).getStringCellValue());
				obj.setCell_19(sscSheet.getRow(i).getCell(19).getStringCellValue());
				obj.setCell_20(sscSheet.getRow(i).getCell(20).getStringCellValue());
				obj.setCell_21(sscSheet.getRow(i).getCell(21).getStringCellValue());
				obj.setCell_22(sscSheet.getRow(i).getCell(22).getStringCellValue());
				obj.setCell_23(sscSheet.getRow(i).getCell(23).getStringCellValue());
				obj.setCell_24(sscSheet.getRow(i).getCell(24).getStringCellValue());

				if (tmraOneTineValue == null && tmraTotal == null) {
					sscSheet.getRow(i).getCell(77).setCellValue("No Activity");
					obj.setThirtheenMonthRollingAvg("No Activity");
				} else if (tmraOneTineValue == null && tmraTotal != null) {
					sscSheet.getRow(i).getCell(77).setCellValue("0%");
					obj.setThirtheenMonthRollingAvg("0%");
				} else if (tmraOneTineValue != null && tmraTotal != null) {

					double result = (tmraOneTineValue * 100) / tmraTotal;
					sscSheet.getRow(i).getCell(77).setCellValue(result + "%");
					obj.setRollingAvg(result);
					obj.setThirtheenMonthRollingAvg(result + "%");
				}

				// column 73-76
				if (onTimeValue != null) {
					sscSheet.getRow(i).getCell(73).setCellValue(onTimeValue);
					obj.setGrandTotalOnTime(onTimeValue + "");
				} else {
					sscSheet.getRow(i).getCell(73).setCellValue(0);
					obj.setGrandTotalOnTime(0 + "");
				}

				if (lateValue != null) {
					sscSheet.getRow(i).getCell(74).setCellValue(lateValue);
					obj.setGrandTotalLate(lateValue + "");
				} else {
					sscSheet.getRow(i).getCell(74).setCellValue(0);
					obj.setGrandTotalLate(0 + "");
				}

				if (lateValue != null && onTimeValue != null) {
					double ytdTotalOtd = (onTimeValue * 100) / (onTimeValue + lateValue);
					sscSheet.getRow(i).getCell(75).setCellValue(ytdTotalOtd + "%");
					obj.setYtdTotal(ytdTotalOtd + "%");
					sscSheet.getRow(i).getCell(76).setCellValue(onTimeValue + lateValue);
					obj.setGrandTotal((onTimeValue + lateValue) + "");

				} else if (onTimeValue == null && lateValue != null) {
					sscSheet.getRow(i).getCell(75).setCellValue("0%");
					obj.setYtdTotal("0%");
					sscSheet.getRow(i).getCell(76).setCellValue(0 + lateValue);
					obj.setGrandTotal((0 + lateValue) + "");
				} else if (onTimeValue != null && lateValue == null) {
					sscSheet.getRow(i).getCell(75).setCellValue("100%");
					obj.setYtdTotal("100%");
					sscSheet.getRow(i).getCell(76).setCellValue(onTimeValue + 0);
					obj.setGrandTotal((onTimeValue + 0) + "");
				} else {
					sscSheet.getRow(i).getCell(75).setCellValue("No Activity");
					obj.setYtdTotal("No Activity");
				}

				sscList.add(obj);
			}

		}

		// sorting obj based on Rolling avg
		List<SupplierScoreCardModel> sortedList = sscList.stream()
				.sorted(Comparator.comparing(SupplierScoreCardModel::getRollingAvg).reversed())
				.collect(Collectors.toList());

		sortedList.forEach(System.out::println);

		// Get the current month in Weekly supplier score card
		Sheet wspcSheet = workbook.getSheetAt(0);

		// Get the month name in full (e.g., "September")
		String monthName = today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		wspcSheet.getRow(1).getCell(30).setCellValue(monthName + " on time");
		wspcSheet.getRow(1).getCell(31).setCellValue(monthName + " Late");

		// copy from supplier score card to weekly supplier score card
		System.out.println(wspcSheet.getFirstRowNum());

		for (i = 0; i < sortedList.size(); i++) {

			wspcSheet.getRow(i + 2).getCell(0).setCellValue(sortedList.get(i).getVendorName());
			wspcSheet.getRow(i + 2).getCell(1).setCellValue(sortedList.get(i).getCell_1());
			wspcSheet.getRow(i + 2).getCell(2).setCellValue(sortedList.get(i).getCell_2());
			wspcSheet.getRow(i + 2).getCell(3).setCellValue(sortedList.get(i).getCell_3());
			wspcSheet.getRow(i + 2).getCell(4).setCellValue(sortedList.get(i).getCell_4());
			wspcSheet.getRow(i + 2).getCell(5).setCellValue(sortedList.get(i).getCell_5());
			wspcSheet.getRow(i + 2).getCell(6).setCellValue(sortedList.get(i).getCell_6());
			wspcSheet.getRow(i + 2).getCell(7).setCellValue(sortedList.get(i).getCell_7());
			wspcSheet.getRow(i + 2).getCell(8).setCellValue(sortedList.get(i).getCell_8());
			wspcSheet.getRow(i + 2).getCell(9).setCellValue(sortedList.get(i).getCell_9());
			wspcSheet.getRow(i + 2).getCell(10).setCellValue(sortedList.get(i).getCell_10());
			wspcSheet.getRow(i + 2).getCell(11).setCellValue(sortedList.get(i).getCell_11());
			wspcSheet.getRow(i + 2).getCell(12).setCellValue(sortedList.get(i).getCell_12());
			wspcSheet.getRow(i + 2).getCell(13).setCellValue(sortedList.get(i).getCell_13());
			wspcSheet.getRow(i + 2).getCell(14).setCellValue(sortedList.get(i).getCell_14());
			wspcSheet.getRow(i + 2).getCell(15).setCellValue(sortedList.get(i).getCell_15());
			wspcSheet.getRow(i + 2).getCell(16).setCellValue(sortedList.get(i).getCell_16());
			wspcSheet.getRow(i + 2).getCell(17).setCellValue(sortedList.get(i).getCell_17());
			wspcSheet.getRow(i + 2).getCell(18).setCellValue(sortedList.get(i).getCell_18());
			wspcSheet.getRow(i + 2).getCell(19).setCellValue(sortedList.get(i).getCell_19());
			wspcSheet.getRow(i + 2).getCell(20).setCellValue(sortedList.get(i).getCell_20());
			wspcSheet.getRow(i + 2).getCell(21).setCellValue(sortedList.get(i).getCell_21());
			wspcSheet.getRow(i + 2).getCell(22).setCellValue(sortedList.get(i).getCell_22());
			wspcSheet.getRow(i + 2).getCell(23).setCellValue(sortedList.get(i).getCell_23());
			wspcSheet.getRow(i + 2).getCell(24).setCellValue(sortedList.get(i).getCell_24());
			wspcSheet.getRow(i + 2).getCell(25).setCellValue(sortedList.get(i).getGrandTotalOnTime());
			wspcSheet.getRow(i + 2).getCell(26).setCellValue(sortedList.get(i).getGrandTotalLate());
			wspcSheet.getRow(i + 2).getCell(27).setCellValue(sortedList.get(i).getYtdTotal());
			wspcSheet.getRow(i + 2).getCell(28).setCellValue(sortedList.get(i).getGrandTotal());
			wspcSheet.getRow(i + 2).getCell(29).setCellValue(sortedList.get(i).getThirtheenMonthRollingAvg());

		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();

		byte[] excelBytes = bos.toByteArray();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=After Market On-Time Delivery 2025.xls");

		return ResponseEntity.ok().headers(headers).contentLength(excelBytes.length)
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
				.body(new ByteArrayResource(excelBytes));
	}

	@GetMapping("/download-supplierCapacityReport")
	public ResponseEntity<ByteArrayResource> downloadCapacityMarketReport()
			throws IOException, EncryptedDocumentException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
		// HSSFWorkbook workbook = new HSSFWorkbook();
		// Populate your workbook with data here

		List<OpenOrder> openOrdersList = wklyOpenOrderRepository.getOpenOrdersList();
		List<CurrentTool> currentToolsList = wklyOpenOrderRepository.getCurrentToolList();

		FileInputStream inputStream = new FileInputStream(
				"C:/Users/mr69509/Downloads/Supplier Capacity Report 07-21-25.xlsx");
		Workbook workbook = WorkbookFactory.create(inputStream);

		CreationHelper createHelper = workbook.getCreationHelper();
		CellStyle cellStyle = workbook.createCellStyle();

		// set month in supplier capacity sheet
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		Sheet spSheet = workbook.getSheetAt(0);
		Row row = spSheet.getRow(0);

		Cell cell = row.getCell(12);
		cell.setCellValue(month + "");

		// Option 1: Using a custom format string
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
		Sheet sheet2 = workbook.getSheetAt(1);
		Sheet sheet4 = workbook.getSheetAt(3);

		int firstRowNum = sheet2.getFirstRowNum();
		int i = 1;
		for (CurrentTool obj : currentToolsList) {
			Row newRow = sheet2.createRow(firstRowNum + i);

			Cell cell1 = newRow.createCell(0);
			cell1.setCellValue(obj.getVendor());

			Cell cell2 = newRow.createCell(1);
			cell2.setCellValue(obj.getDate());
			cell2.setCellStyle(cellStyle);

			Cell cell3 = newRow.createCell(2);
			cell3.setCellValue(obj.getTotal());

			i++;

		}
		firstRowNum = sheet4.getFirstRowNum();
		i = 1;
		for (OpenOrder obj : openOrdersList) {
			Row newRow = sheet4.createRow(firstRowNum + i);

			Cell cell1 = newRow.createCell(0);
			cell1.setCellValue(obj.getVendorName());

			Cell cell2 = newRow.createCell(1);
			cell2.setCellValue(obj.getOpenQuantity());

			i++;

		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();

		byte[] excelBytes = bos.toByteArray();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=After Market On-Time Delivery 2025.xls");

		return ResponseEntity.ok().headers(headers).contentLength(excelBytes.length)
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
				.body(new ByteArrayResource(excelBytes));
	}

}
