package com.vtes.security.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vtes.entity.RefreshToken;
import com.vtes.exception.TokenRefreshException;
import com.vtes.repository.RefreshTokenRepository;
import com.vtes.repository.UserRepository;

@Service
public class RefreshTokenService {
	@Value("${vtes.app.jwtRefreshExpiration}")
	private Long refreshTokenDuration;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserRepository userRepository;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken createRefreshToken(Integer userId) {
		RefreshToken refreshToken = new RefreshToken();
		if (refreshTokenRepository.findByUserId(userId).isEmpty()) {
			refreshToken.setUser(userRepository.findById(userId).get());
			refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDuration*1000));
			refreshToken.setToken(UUID.randomUUID().toString());
			refreshToken = refreshTokenRepository.save(refreshToken);
			return refreshToken;
		} else {
			refreshToken = refreshTokenRepository.findByUserId(userId).get();
			refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDuration*1000));
			refreshToken.setToken(UUID.randomUUID().toString());
			refreshToken = refreshTokenRepository.save(refreshToken);
			return refreshToken;
		}

	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			throw new TokenRefreshException(token.getToken(),
					"Refresh token was expired. Please make a new signin request");
		}

		return token;
	}

	@Transactional
	public int deleteByUserId(Integer userId) {
		return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
	}
}
