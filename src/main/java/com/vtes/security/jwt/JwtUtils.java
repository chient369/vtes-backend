package com.vtes.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vtes.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${vtes.app.jwtSecret}")
	private String jwtSecret;

	@Value("${vtes.app.jwtExpiration}")
	private Integer jwtExpiration;
	
	@Value("${vtes.app.verifyCodeExpiration}")
	private Integer verifyCodeExpiration;

	public String generateJwtToken(UserDetailsImpl userPrincipal) {
		return generateTokenFromEmail(userPrincipal.getEmail());
	}

	public String generateTokenFromEmail(String email) {
		return Jwts.builder().setSubject(email).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpiration*1000))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public String generateTokenToActiveUser(String email) {
		return Jwts.builder().setSubject(email).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + verifyCodeExpiration*1000)).signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public String generateTokenToResetPassword(String email) {
		return Jwts.builder().setSubject(email).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + verifyCodeExpiration*1000)).signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {

		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);

			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

}
