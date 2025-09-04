package com.example.after_market_db_tool.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class SecurityUtils {
	
	public static boolean verifyPasssword(String plain, String hashed) {
		return BCrypt.checkpw(plain, hashed);
		
	}
	
	public static String saltAndHashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

}
