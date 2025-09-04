package com.example.after_market_db_tool.model;

import com.example.after_market_db_tool.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public class LoginResponse {

	private CreateUserRequest user;
	private String errorMessage;
	private String token;	
	private Boolean isSuccess = Boolean.FALSE;
	private String vendorCode;
	
	public String getVendorCode() {
		return vendorCode;
	}
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}
	public Boolean getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public CreateUserRequest getUser() {
		return user;
	}
	public void setUser(CreateUserRequest user) {
		this.user = user;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	

    public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
}