package com.example.after_market_db_tool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.after_market_db_tool.entity.Otp;
import com.example.after_market_db_tool.repository.OtpRepository;
import com.example.after_market_db_tool.util.OtpGenerator;

@Service
public class OtpService {
	// In a real application, this would interact with a database or cache
	// private static Otp storedOtp; // For demonstration, storing a single OTP

	@Autowired
	OtpRepository otpRepository;

	public String generateAndStoreOtp(String userId, long expirationMinutes) {
		String otpCode = OtpGenerator.generateOtp();
		Otp otp = new Otp(otpCode, userId, expirationMinutes);
		// In a real system, save to database/cache with userId as key
		otpRepository.save(otp);
		return otpCode;
	}

	public boolean validateOtp(String userId, String submittedOtp) {
		Otp storedOtp = otpRepository.findByUserId(userId);
		if (storedOtp == null || !storedOtp.getUserId().equals(userId)) {
			return false; // No OTP found for this user
		}

		if (storedOtp.isExpired()) {
			// Optionally, remove expired OTP from storage
			otpRepository.delete(storedOtp);
			return false; // OTP expired
		}

		if (storedOtp.getCode().equals(submittedOtp)) {
			// OTP is valid and not expired, clear it after successful use
			otpRepository.delete(storedOtp);
			return true;
		}
		return false; // Incorrect OTP
	}

	public Otp getOtpById(String userId) {
		return otpRepository.findByUserId(userId);
	}

	public void deleteOtp(Otp otp) {
		otpRepository.delete(otp);
	}
}