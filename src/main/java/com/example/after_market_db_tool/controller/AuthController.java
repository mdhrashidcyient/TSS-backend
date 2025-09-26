package com.example.after_market_db_tool.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.after_market_db_tool.entity.Otp;
import com.example.after_market_db_tool.entity.UserEntity;
import com.example.after_market_db_tool.entity.VerificationToken;
import com.example.after_market_db_tool.model.CreateUserRequest;
import com.example.after_market_db_tool.model.LoginRequest;
import com.example.after_market_db_tool.model.LoginResponse;
import com.example.after_market_db_tool.model.OtpRequest;
import com.example.after_market_db_tool.repository.UserRepository;
import com.example.after_market_db_tool.repository.VerificationRepository;
import com.example.after_market_db_tool.security.SecurityUtils;
import com.example.after_market_db_tool.service.JwtService;
import com.example.after_market_db_tool.service.OtpService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "*", allowedHeaders = "*")
@CrossOrigin(origins = "https://tooling.cyient.com/")
public class AuthController {

	@Value("${admin.email}")
	private String adminEmail;

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private VerificationRepository tokenRepo;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private OtpService otpService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody CreateUserRequest req, HttpServletRequest servletRequest) {
		if (userRepo.existsByEmail(req.getEmail())) {
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
		user.setEnabled(false);
		userRepo.save(user);

		// Create token
		String jwtToekn = jwtService.generateToken(req.getEmail());
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(jwtToekn);
		verificationToken.setUser(user);
		verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
		tokenRepo.save(verificationToken);

		// Build confirmation URL
		String appUrl = servletRequest.getRequestURL().toString().replace(servletRequest.getRequestURI(), "");
		String confirmUrl = appUrl + "/api/auth/confirm?token=" + jwtToekn;

		// Send email
		sendEmail(user.getEmail(), confirmUrl);

		return ResponseEntity.ok("Registration successful. Check your email to confirm.");
	}

	private void sendEmail(String to, String confirmUrl) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(to);
		mail.setSubject("Confirm your email");
		mail.setText("Click to confirm: " + confirmUrl);
		mailSender.send(mail);
	}

	@GetMapping("/confirm")
	public ResponseEntity<String> confirm(@RequestParam String token) {
		VerificationToken vt = tokenRepo.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));

		if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
			return ResponseEntity.badRequest().body("Token expired.");
		}

		UserEntity user = vt.getUser();
		// user.setEnabled(false);
		// userRepo.save(user);
		sendEmailToAdmin(adminEmail, user.getEmail());
		tokenRepo.delete(vt);

		return ResponseEntity.ok("Email confirmed. Awaiting for Admin approval.");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		try {
			// Authenticate the user
//            Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                    request.getEmail(), request.getPassword()
//                )
//            );
			
			Otp otp = otpService.getOtpById(request.getEmail());
			
			if(otp!= null){
				otpService.deleteOtp(otp);
			}

			LoginResponse response = new LoginResponse();

			UserEntity user = userRepo.findByEmail(request.getEmail())
					.orElseThrow(() -> new RuntimeException("User not found"));

			if (!user.isEnabled()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please confirm your email first.");
			}

			if (!SecurityUtils.verifyPasssword(request.getPassword(), user.getPassword())) {
				response.setIsSuccess(Boolean.FALSE);
				response.setErrorMessage("Incorrect Password");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}

			String otpCode = otpService.generateAndStoreOtp(request.getEmail(), 15);
			// String jwt = jwtService.generateToken(authentication.getName());
			// String jwtToken = jwtService.generateToken(request.getEmail());

			// LoginResponse loginResponse = convert(user,jwtToken);

			// return ResponseEntity.ok(loginResponse);
			// Send email
			sendOtpByEmail(request.getEmail(), otpCode);
			return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body("Please check your email for OTP");

		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
		}
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest request) {
		try {
			// Authenticate the user
//            Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                    request.getEmail(), request.getPassword()
//                )
//            );

			LoginResponse response = new LoginResponse();
			UserEntity user = userRepo.findByEmail(request.getEmail())
					.orElseThrow(() -> new RuntimeException("User not found"));

			if (otpService.validateOtp(request.getEmail(), request.getOtp())) {

				String jwtToken = jwtService.generateToken(request.getEmail());

				LoginResponse loginResponse = convert(user, jwtToken);
				return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
			}

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please enter valid OTP");

		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP.");
		}
	}

	private LoginResponse convert(UserEntity user, String jwtToken) {
		LoginResponse response = new LoginResponse();
		CreateUserRequest userResponse = new CreateUserRequest();
		userResponse.setFirstName(user.getFirstName());
		userResponse.setLastName(user.getLastName());
		userResponse.setEmail(user.getEmail());
		userResponse.setRole(user.getRole());
		userResponse.setVendorCode(user.getVendorCode());
		response.setToken(jwtToken);
		response.setIsSuccess(Boolean.TRUE);
		response.setUser(userResponse);
		return response;
	}

	private void sendEmailToAdmin(String to, String email) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(to);
		mail.setSubject("New Account is pending for approval.");
		mail.setText("Please arppove new account -" + email);
		mailSender.send(mail);
	}

	private void sendOtpByEmail(String to, String otpCode) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(to);
		mail.setSubject("Otp");
		mail.setText("Otp Code: " + otpCode);
		mailSender.send(mail);
	}
}