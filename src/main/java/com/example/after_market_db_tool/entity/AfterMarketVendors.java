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
@Table(name="AfterMarketVendors")


public class AfterMarketVendors {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	private String supplier;
	 private String analystSupporting;
	 private String address;
	 private String vendorCode;
	 private String commutingVendor;
	 private String niche;
	 private String email;
	 private String contactName;
	 private String phone;
	 private String wipEmail;
	 private String comments;
	 private String active;
	 public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getAnalystSupporting() {
		return analystSupporting;
	}
	public void setAnalystSupporting(String analystSupporting) {
		this.analystSupporting = analystSupporting;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	public String getCommutingVendor() {
		return commutingVendor;
	}
	public void setCommutingVendor(String commutingVendor) {
		this.commutingVendor = commutingVendor;
	}
	public String getNiche() {
		return niche;
	}
	public void setNiche(String niche) {
		this.niche = niche;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getWipEmail() {
		return wipEmail;
	}
	public void setWipEmail(String wipEmail) {
		this.wipEmail = wipEmail;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}

	 
}