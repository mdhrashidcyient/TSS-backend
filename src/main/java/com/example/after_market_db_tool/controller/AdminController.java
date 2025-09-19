package com.example.after_market_db_tool.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.after_market_db_tool.entity.UserEntity;
import com.example.after_market_db_tool.model.CreateUserRequest;
import com.example.after_market_db_tool.repository.UserRepository;
import com.example.after_market_db_tool.repository.VerificationRepository;
import com.example.after_market_db_tool.security.SecurityUtils;
import com.example.after_market_db_tool.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin")
//@CrossOrigin(origins = "*", allowedHeaders = "*")
@CrossOrigin(origins = "http://tooling.cyient.com")

public class AdminController {
	@Autowired
	UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private VerificationRepository tokenRepo;
	
	@Autowired private JavaMailSender mailSender;
	
	
	@GetMapping("/users")
	public List<UserEntity> getAllUsers() {
		return userRepository.findAll();
	}

	@PostMapping("/registerAdmin")
	public ResponseEntity<?> registerAdmin(@RequestBody CreateUserRequest req, HttpServletRequest servletRequest) {
		if (userRepository.existsByEmail(req.getEmail())) {
			return ResponseEntity.badRequest().body("Email already in use.");
		}

		String hashedPw = SecurityUtils.saltAndHashPassword(req.getPassword());

		UserEntity user = new UserEntity();
		user.setFirstName(req.getFirstName());
		user.setLastName(req.getLastName());
		user.setEmail(req.getEmail());
		user.setPhoneNumber(req.getPhoneNumber());
		user.setCompanyName(req.getCompanyName());
		user.setRole(req.getRole());
		user.setVendorCode(req.getVendorCode());
		user.setPassword(hashedPw);
		user.setEnabled(true);
		userRepository.save(user);

//        // Create token
//        String jwtToekn = jwtService.generateToken(req.getEmail());
//        VerificationToken verificationToken = new VerificationToken();
//        verificationToken.setToken(jwtToekn);
//        verificationToken.setUser(user);
//        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
//        tokenRepo.save(verificationToken);
//
//        // Build confirmation URL
//        String appUrl = servletRequest.getRequestURL().toString().replace(servletRequest.getRequestURI(), "");
//        String confirmUrl = appUrl + "/api/auth/confirm?token=" + jwtToekn;
//
//        // Send email
//        sendEmail(user.getEmail(), confirmUrl);

		return ResponseEntity.ok("Registration successful.");
	}

	@PostMapping("/enableUser")
	public ResponseEntity<?> enableUser(@RequestBody CreateUserRequest req, HttpServletRequest servletRequest) {

		UserEntity user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
		user.setEnabled(true);

		userRepository.save(user);

//        // Create token
//        String jwtToekn = jwtService.generateToken(req.getEmail());
//        VerificationToken verificationToken = new VerificationToken();
//        verificationToken.setToken(jwtToekn);
//        verificationToken.setUser(user);
//        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
//        tokenRepo.save(verificationToken);
//
//        // Build confirmation URL
//        String appUrl = servletRequest.getRequestURL().toString().replace(servletRequest.getRequestURI(), "");
        String confirmUrl = "Your account is active now. ";

		// Send email
		sendConfirmationEmail(user.getEmail());

		return ResponseEntity.ok("Registration successful.");
	}
	
	
    private void sendConfirmationEmail(String to) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Account is active.Please login.");
        mail.setText("Your account is active now. " );
        mailSender.send(mail);
    }
    

}
