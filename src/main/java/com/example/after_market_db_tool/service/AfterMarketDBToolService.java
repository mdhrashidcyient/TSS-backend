package com.example.after_market_db_tool.service;

import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.after_market_db_tool.dto.WklyOpenOrderDto;
import com.example.after_market_db_tool.entity.CurrentWeekOpenOrder;
import com.example.after_market_db_tool.entity.OpenOrderPrev;
import com.example.after_market_db_tool.entity.OtdData;
import com.example.after_market_db_tool.entity.PrevWeekAnalystReport;
import com.example.after_market_db_tool.entity.WklyOpenOrderTemp;
import com.example.after_market_db_tool.repository.AfterMarketDBToolRepository;
import com.example.after_market_db_tool.repository.OtdDataRepository;
import com.example.after_market_db_tool.repository.PrevOpenOrderRepository;
import com.example.after_market_db_tool.repository.PrevWeekAnalystReportRepository;
import com.example.after_market_db_tool.repository.WklyOpenOrderRepository;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class AfterMarketDBToolService {

	@Autowired
	AfterMarketDBToolRepository afterMarketDBToolRepository;

	@Autowired
	PrevOpenOrderRepository prevOpenOrderRepository;

	@Autowired
	WklyOpenOrderRepository wklyOpenOrderRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PrevWeekAnalystReportRepository prevWeekAnalystReportRepository;
	
	@Autowired
	private OtdDataRepository otdDataRepository;



	public void refreshDBWithWIPData(String excelPath) throws InvalidFormatException, IOException,
			EncryptedDocumentException, org.apache.poi.openxml4j.exceptions.InvalidFormatException, ParseException {
		Workbook workbook = WorkbookFactory.create(new File(excelPath));
		String pattern = "MM/dd/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date targetDate = new Date();
		try {
			targetDate = sdf.parse("01/01/2019");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		afterMarketDBToolRepository.deleteAll();

		for (Sheet sheet : workbook) {

			for (int i = 1; i < sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);

				if (!isRowEmpty(row)) {

					String srcDept = row.getCell(0).getStringCellValue();
					String material = row.getCell(8).getStringCellValue();

					Date dDate = row.getCell(16).getDateCellValue();
					String formattedDate = sdf.format(dDate);
					DataFormatter formatter = new DataFormatter();

					Date deliveryDate = sdf.parse(formattedDate);

					if (srcDept.equals("TSB") || srcDept.equals("TSC") || srcDept.equals("TSD")
							|| srcDept.equals("TSE")) {
						if (!material.equals("SERVICE") || !material.equals("service")) {
							if (deliveryDate.after(targetDate)) {
								WklyOpenOrderTemp obj = new WklyOpenOrderTemp();
								obj.setSourceDepartment(srcDept);
								obj.setBuyerName(row.getCell(1).getStringCellValue());
								obj.setDocType(row.getCell(2).getStringCellValue());
								obj.setVendorCode(row.getCell(3).getStringCellValue());
								obj.setVendorName(row.getCell(4).getStringCellValue());
								if (!formatter.formatCellValue(row.getCell(5)).contains("#")) {
									obj.setApprovalDate(row.getCell(5).getDateCellValue());
								} else {
									obj.setApprovalDate(null);
								}
								obj.setpONumber(row.getCell(6).getStringCellValue());
								obj.setpOLineItem(row.getCell(7).getStringCellValue());
								obj.setMaterial(material);
								obj.setMaterialDesc(row.getCell(9).getStringCellValue());
								obj.setWbsElement(row.getCell(12).getStringCellValue());
								obj.setItemCategory(row.getCell(13).getStringCellValue());
								obj.setPriceType(row.getCell(14).getStringCellValue());
								obj.setContractNumber(row.getCell(15).getStringCellValue());
								obj.setDeliveryDate(row.getCell(16).getDateCellValue());
								obj.setStatisticsDate(row.getCell(17).getDateCellValue());
								obj.setItemQuantity((int) row.getCell(18).getNumericCellValue());
								obj.setgRQuantity((int) row.getCell(19).getNumericCellValue());
								obj.setOpenQuantity((int) row.getCell(20).getNumericCellValue());

								afterMarketDBToolRepository.save(obj);

							}
						}

					}

				}
			}

		}
		workbook.close();

	}

	public void saveDataIntoWklyOpenOrder() throws SQLException {

		wklyOpenOrderRepository.deleteAll();
		System.out.println("data deleted");
		wklyOpenOrderRepository.callStoredProcedure();

		System.out.println("SP executed");
	}

	public List<CurrentWeekOpenOrder> getOrdersByAnalyst(String analyst) {
		return wklyOpenOrderRepository.findByCyientAnalyst(analyst);
	}

	public List<CurrentWeekOpenOrder> getOrdersByVendorCode(String vendorCode) {
		return wklyOpenOrderRepository.findByVendorCode(vendorCode);
	}

	public void uploadPrevOpenOrderData(String excelPath) throws InvalidFormatException, IOException,
			EncryptedDocumentException, org.apache.poi.openxml4j.exceptions.InvalidFormatException, ParseException {
		Workbook workbook = WorkbookFactory.create(new File(excelPath));
		// String pattern = "MM/dd/yyyy";
		// SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		// Date targetDate = new Date();
		// try {
		// targetDate = sdf.parse("01/01/2019");
		// } catch (ParseException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		prevOpenOrderRepository.deleteAll();

		for (Sheet sheet : workbook) {

			for (int i = 1; i < sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);

				if (!isRowEmpty(row)) {

					// String srcDept = row.getCell(0).getStringCellValue();
					// String material = row.getCell(8).getStringCellValue();

//					Date dDate = row.getCell(16).getDateCellValue();
					// String formattedDate = sdf.format(dDate);
					DataFormatter formatter = new DataFormatter();

//					Date deliveryDate = sdf.parse(formattedDate);
//
//					if (srcDept.equals("TSB") || srcDept.equals("TSC") || srcDept.equals("TSD")
//							|| srcDept.equals("TSE")) {
//						if (!material.equals("SERVICE") || !material.equals("service")) {
//							if (deliveryDate.after(targetDate)) {
					OpenOrderPrev obj = new OpenOrderPrev();
					obj.setSourceDepartment(row.getCell(0).getStringCellValue());
					if (row.getCell(1) != null) {
						obj.setBuyerName(row.getCell(1).getStringCellValue());
					} else {
						obj.setBuyerName(null);
					}
					if (row.getCell(2) != null) {
						obj.setDocType(row.getCell(2).getStringCellValue());
					} else {
						obj.setDocType(null);
					}
					if (row.getCell(3) != null) {
						obj.setVendorCode(row.getCell(3).getStringCellValue());
					} else {
						obj.setVendorCode(null);
					}
					if (row.getCell(4) != null) {
						obj.setVendorName(row.getCell(4).getStringCellValue());
					} else {
						obj.setVendorName(null);
					}

					if (row.getCell(5) != null) {
						obj.setApprovalDate(row.getCell(5).getDateCellValue());
					} else {
						obj.setApprovalDate(null);
					}
					if (row.getCell(6) != null) {
						obj.setpONumber(row.getCell(6).getStringCellValue());
					} else {
						obj.setpONumber(null);
					}

					if (row.getCell(7) != null) {
						obj.setpOLineItem(row.getCell(7).getStringCellValue());
					} else {
						obj.setpOLineItem(null);
					}
					if (row.getCell(8) != null) {
						obj.setMaterial(row.getCell(8).getStringCellValue());
					} else {
						obj.setMaterial(null);
					}
					if (row.getCell(9) != null) {
						obj.setMaterialDesc(row.getCell(9).getStringCellValue());
					} else {
						obj.setMaterialDesc(null);
					}

					if (row.getCell(10) != null) {
						obj.setWbsElement(row.getCell(10).getStringCellValue());
					} else {
						obj.setWbsElement(null);
					}
					if (row.getCell(11) != null) {
						obj.setItemCategory(row.getCell(11).getStringCellValue());
					} else {
						obj.setItemCategory(null);
					}
					if (row.getCell(12) != null) {
						obj.setPriceType(row.getCell(12).getStringCellValue());
					} else {
						obj.setPriceType(null);
					}

					if (row.getCell(13) != null) {
						obj.setContractNumber(row.getCell(13).getStringCellValue());
					} else {
						obj.setContractNumber(null);
					}

					if (row.getCell(14) != null) {
						obj.setDeliveryDate(row.getCell(14).getDateCellValue());
					} else {
						obj.setDeliveryDate(null);
					}

					if (row.getCell(15) != null) {
						obj.setStatisticsDate(row.getCell(15).getDateCellValue());
					} else {
						obj.setStatisticsDate(null);
					}
					if (row.getCell(16) != null) {
						obj.setItemQuantity((int) row.getCell(16).getNumericCellValue());
					} else {
						obj.setItemQuantity(0);
					}
					if (row.getCell(17) != null) {
						obj.setgRQuantity((int) row.getCell(17).getNumericCellValue());
					} else {
						obj.setgRQuantity(0);
					}
					if (row.getCell(18) != null) {
						obj.setOpenQuantity((int) row.getCell(18).getNumericCellValue());
					} else {
						obj.setOpenQuantity(0);
					}
					if (row.getCell(19) != null) {
						obj.setPrevSupplierComments(row.getCell(19).getStringCellValue());
					} else {
						obj.setPrevSupplierComments(null);
					}
					if (row.getCell(20) != null) {
						obj.setCurrentSupplierComments(row.getCell(20).getStringCellValue());
					} else {
						obj.setCurrentSupplierComments(null);
					}
					if (row.getCell(21) != null) {
						obj.setpOReceived(row.getCell(21).getStringCellValue());
					} else {
						obj.setpOReceived(null);
					}
					if (row.getCell(22) != null) {
						obj.setSupplierInternalCode(row.getCell(22).getStringCellValue());
					} else {
						obj.setSupplierInternalCode(null);
					}
					if (row.getCell(23) != null) {
						obj.setCurrentStatisticsDate(row.getCell(23).getStringCellValue());
					} else {
						obj.setCurrentStatisticsDate(null);
					}
					if (row.getCell(24) != null) {
						obj.setCyientAnalyst(row.getCell(24).getStringCellValue());
					} else {
						obj.setCyientAnalyst(null);
					}
					if (row.getCell(25) != null) {
						obj.setSanctionCheck(row.getCell(25).getStringCellValue());
					} else {
						obj.setSanctionCheck(null);
					}

					prevOpenOrderRepository.save(obj);

					// }
					// }

					// }

				}
			}

		}
		workbook.close();

	}

	public void uploadPrevWeekAnalystData(String excelPath) throws InvalidFormatException, IOException,
			EncryptedDocumentException, org.apache.poi.openxml4j.exceptions.InvalidFormatException, ParseException {
		Workbook workbook = WorkbookFactory.create(new File(excelPath));

		// prevWeekAnalystReportRepository.deleteAll();

		for (Sheet sheet : workbook) {

			for (int i = 0; i < sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);

				if (!isRowEmpty(row)) {
					PrevWeekAnalystReport obj = new PrevWeekAnalystReport();

					if (row.getCell(0) != null) {
						obj.setPoNumber(row.getCell(0).getStringCellValue());
					} else {
						obj.setPoNumber(null);
					}

					if (row.getCell(1) != null) {
						obj.setPoLineItem(row.getCell(1).getStringCellValue());
					} else {
						obj.setPoLineItem(null);
					}

					if (row.getCell(2) != null) {
						obj.setLastWeekAnalystComments(row.getCell(2).getStringCellValue());
					} else {
						obj.setLastWeekAnalystComments(null);
					}

					if (row.getCell(3) != null) {
						obj.setCurrentWeekAnalystComments(row.getCell(3).getStringCellValue());
					} else {
						obj.setCurrentWeekAnalystComments(null);
					}

					prevWeekAnalystReportRepository.save(obj);

				}
			}

		}
		workbook.close();

	}

	public void uploadOtdData(String excelPath) throws InvalidFormatException, IOException,
			EncryptedDocumentException, org.apache.poi.openxml4j.exceptions.InvalidFormatException, ParseException {
		Workbook workbook = WorkbookFactory.create(new File(excelPath));

		otdDataRepository.deleteAll();

		for (Sheet sheet : workbook) {

			for (int i = 1; i < sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);

				if (!isRowEmpty(row)) {

					OtdData obj = new OtdData();
					
					if (row.getCell(0) != null) {
						obj.setVendorCode(row.getCell(0).getStringCellValue());
					}else {
						obj.setVendorCode(null);
					}
					
					if (row.getCell(1) != null) {
						obj.setVendorName(row.getCell(1).getStringCellValue());
					} else {
						obj.setVendorName(null);
					}
					if (row.getCell(2) != null) {
						obj.setPlant(row.getCell(2).getStringCellValue());
					} else {
						obj.setPlant(null);
					}
					if (row.getCell(3) != null) {
						obj.setPlant_(row.getCell(3).getStringCellValue());
					} else {
						obj.setPlant_(null);
					}
					if (row.getCell(4) != null) {
						obj.setPurchGroup(row.getCell(4).getStringCellValue());
					} else {
						obj.setPurchGroup(null);
					}

					if (row.getCell(5) != null) {
						obj.setBuyerName(row.getCell(5).getStringCellValue());
					} else {
						obj.setBuyerName(null);
					}
					if (row.getCell(6) != null) {
						obj.setRegion(row.getCell(6).getStringCellValue());
					} else {
						obj.setRegion(null);
					}

					if (row.getCell(7) != null) {
						obj.setSize(row.getCell(7).getStringCellValue());
					} else {
						obj.setSize(null);
					}
					if (row.getCell(8) != null) {
						obj.setSocio(row.getCell(8).getStringCellValue());
					} else {
						obj.setSocio(null);
					}
					if (row.getCell(9) != null) {
						obj.setPoNumber(row.getCell(9).getStringCellValue());
					} else {
						obj.setPoNumber(null);
					}

					if (row.getCell(10) != null) {
						obj.setItem(row.getCell(10).getStringCellValue());
					} else {
						obj.setItem(null);
					}
					if (row.getCell(11) != null) {
						obj.setCompletionCode(row.getCell(11).getStringCellValue());
					} else {
						obj.setCompletionCode(null);
					}
					if (row.getCell(12) != null) {
						obj.setAcctCat(row.getCell(12).getStringCellValue());
					} else {
						obj.setAcctCat(null);
					}

					if (row.getCell(13) != null) {
						obj.setItemCat(row.getCell(13).getStringCellValue());
					} else {
						obj.setItemCat(null);
					}

					if (row.getCell(14) != null) {
						obj.setMaterial(row.getCell(14).getStringCellValue());
					} else {
						obj.setMaterial(null);
					}

					if (row.getCell(15) != null) {
						obj.setMaterialType(row.getCell(15).getStringCellValue());
					} else {
						obj.setMaterialType(null);
					}
					if (row.getCell(16) != null) {
						obj.setDescription(row.getCell(16).getStringCellValue());
					} else {
						obj.setDescription(null);
					}
					if (row.getCell(17) != null || !row.getCell(17).equals("#")) {
						obj.setDocumentVersion(row.getCell(17).getStringCellValue());
					} else {
						obj.setDocumentVersion(null);
					}
					if (row.getCell(18) != null || !row.getCell(18).equals("#")  ) {
						obj.setWbsElement(row.getCell(18).getStringCellValue());
					} else {
						obj.setWbsElement(null);
					}
					if (row.getCell(19) != null || !row.getCell(19).equals("#")  ) {
						obj.setContact(row.getCell(19).getStringCellValue());
					} else {
						obj.setContact(null);
					}
					if (row.getCell(20) != null) {
						obj.setDeliveryDate(row.getCell(20).getDateCellValue());
					} else {
						obj.setDeliveryDate(null);
					}
					if (row.getCell(21) != null) {
						obj.setGrDate(row.getCell(21).getDateCellValue());
					} else {
						obj.setGrDate(null);
					}
					if (row.getCell(22) != null) {
						obj.setMaterialGroup(row.getCell(22).getStringCellValue());
					} else {
						obj.setMaterialGroup(null);
					}
					if (row.getCell(23) != null) {
						obj.setMaterialDescription(row.getCell(23).getStringCellValue());
					} else {
						obj.setMaterialDescription(null);
					}
					if (row.getCell(24) != null) {
						obj.setItemQuantity((int)row.getCell(24).getNumericCellValue());
					} else {
						obj.setItemQuantity(0);
					}
					if (row.getCell(25) != null) {
						obj.setItemValue((int)row.getCell(25).getNumericCellValue());
					} else {
						obj.setItemValue(0);
					}

					otdDataRepository.save(obj);


				}
			}

		}
		workbook.close();

	}

	public boolean isRowEmpty(Row row) {
		if (row == null) {
			return true;
		}
		for (Cell cell : row) {
			// Check if the cell is not blank and contains non-whitespace content
			if (cell != null && !cell.toString().trim().isEmpty()) {
				return false; // Found data in a cell, so the row is not empty

			}
		}
		return true;
	}

}
