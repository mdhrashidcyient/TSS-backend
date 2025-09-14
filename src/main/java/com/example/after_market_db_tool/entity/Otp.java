package com.example.after_market_db_tool.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="Otp")
public class Otp {
	
	@Id @GeneratedValue
    private Long id;
	
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String userId; // Or email/phone number
    
    public Otp() {
    	
    }

    public Otp(String code, String userId, long expirationMinutes) {
        this.code = code;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMinutes(expirationMinutes);
    }

    // Getters for code, createdAt, expiresAt, userId
    public String getCode() { return code; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public String getUserId() { return userId; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
