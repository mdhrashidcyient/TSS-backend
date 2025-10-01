package com.example.after_market_db_tool.model;

import lombok.Data;

@Data
public class ResetPasswordRequest {
	
	private String email;
	private String password;

	public ResetPasswordRequest() {
		// TODO Auto-generated constructor stub
	}
	
	   public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}

}
