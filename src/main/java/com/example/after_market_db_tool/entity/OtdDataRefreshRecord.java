package com.example.after_market_db_tool.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="OtdDataRefreshRecord")
public class OtdDataRefreshRecord {
	
	@Id @GeneratedValue
    private Long id;
	
  

	private String userId;
    private String refreshedAt;

	public OtdDataRefreshRecord() {
		// TODO Auto-generated constructor stub
	}
	
	  public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getUserid() {
			return userId;
		}

		public void setUserId(String userid) {
			this.userId = userId;
		}

		public String getRefreshedAt() {
			return refreshedAt;
		}

		public void setRefreshedAt(String refreshedAt) {
			this.refreshedAt = refreshedAt;
		}

}

