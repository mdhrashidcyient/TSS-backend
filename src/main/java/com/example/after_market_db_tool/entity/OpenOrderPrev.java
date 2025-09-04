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
@Table(name = "OpenOrderPrev")

public class OpenOrderPrev {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String sourceDepartment;
	private String buyerName;
	private String docType;
	private String vendorCode;
	private String vendorName;
	private Date approvalDate;
	private String pONumber;
	private String pOLineItem;
	private String material;
	private String materialDesc;
	private String wbsElement;
	private String itemCategory;
	private String priceType;
	private String contractNumber;
	private Date deliveryDate;
	private Date statisticsDate;
	private int itemQuantity;
	private int gRQuantity;
	private int openQuantity;
	private String prevSupplierComments;
	private String currentSupplierComments;
	private String pOReceived;
	private String supplierInternalCode;
	private String currentStatisticsDate;
	private String cyientAnalyst;
	private String sanctionCheck;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSourceDepartment() {
		return sourceDepartment;
	}

	public void setSourceDepartment(String sourceDepartment) {
		this.sourceDepartment = sourceDepartment;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public Date getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(Date approvalDate) {
		this.approvalDate = approvalDate;
	}

	public String getpONumber() {
		return pONumber;
	}

	public void setpONumber(String pONumber) {
		this.pONumber = pONumber;
	}

	public String getpOLineItem() {
		return pOLineItem;
	}

	public void setpOLineItem(String pOLineItem) {
		this.pOLineItem = pOLineItem;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getMaterialDesc() {
		return materialDesc;
	}

	public void setMaterialDesc(String materialDesc) {
		this.materialDesc = materialDesc;
	}

	public String getWbsElement() {
		return wbsElement;
	}

	public void setWbsElement(String wbsElement) {
		this.wbsElement = wbsElement;
	}

	public String getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Date getStatisticsDate() {
		return statisticsDate;
	}

	public void setStatisticsDate(Date statisticsDate) {
		this.statisticsDate = statisticsDate;
	}

	public int getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	public int getgRQuantity() {
		return gRQuantity;
	}

	public void setgRQuantity(int gRQuantity) {
		this.gRQuantity = gRQuantity;
	}

	public int getOpenQuantity() {
		return openQuantity;
	}

	public void setOpenQuantity(int openQuantity) {
		this.openQuantity = openQuantity;
	}

	public String getPrevSupplierComments() {
		return prevSupplierComments;
	}

	public void setPrevSupplierComments(String prevSupplierComments) {
		this.prevSupplierComments = prevSupplierComments;
	}

	public String getCurrentSupplierComments() {
		return currentSupplierComments;
	}

	public void setCurrentSupplierComments(String currentSupplierComments) {
		this.currentSupplierComments = currentSupplierComments;
	}

	public String getpOReceived() {
		return pOReceived;
	}

	public void setpOReceived(String pOReceived) {
		this.pOReceived = pOReceived;
	}

	public String getSupplierInternalCode() {
		return supplierInternalCode;
	}

	public void setSupplierInternalCode(String supplierInternalCode) {
		this.supplierInternalCode = supplierInternalCode;
	}

	public String getCurrentStatisticsDate() {
		return currentStatisticsDate;
	}

	public void setCurrentStatisticsDate(String
			currentStatisticsDate) {
		this.currentStatisticsDate = currentStatisticsDate;
	}

	public String getCyientAnalyst() {
		return cyientAnalyst;
	}

	public void setCyientAnalyst(String cyientAnalyst) {
		this.cyientAnalyst = cyientAnalyst;
	}

	public String getSanctionCheck() {
		return sanctionCheck;
	}

	public void setSanctionCheck(String sanctionCheck) {
		this.sanctionCheck = sanctionCheck;
	}

}

