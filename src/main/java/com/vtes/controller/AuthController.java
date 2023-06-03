package com.vtes.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vtes.entity.RefreshToken;
import com.vtes.entity.User;
import com.vtes.exception.AuthenticationFailedException;
import com.vtes.exception.TokenRefreshException;
import com.vtes.exception.UserException;
import com.vtes.exception.VtesException;
import com.vtes.model.ResponseData;
import com.vtes.model.ResponseData.ResponseType;
import com.vtes.payload.LoginPayload;
import com.vtes.payload.RegisterPayload;
import com.vtes.security.jwt.CookieUtils;
import com.vtes.security.jwt.JwtUtils;
import com.vtes.security.service.RefreshTokenService;
import com.vtes.security.service.UserDetailsImpl;
import com.vtes.service.UserServiceImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private CookieUtils cookieUtils;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginPayload loginRequest,
			HttpServletResponse httpServletResponse) throws VtesException {

		Authentication authentication = null;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		} catch (Exception e) {
			throw new AuthenticationFailedException(loginRequest.getEmail());
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		if (!userService.isActiveUserAccount(userDetails.getEmail())) {
			throw new UserException("API001_ER02", "This account is not active yet");

		}

		String jwt = jwtUtils.generateJwtToken(userDetails);

		if (loginRequest.isRemember()) {
			RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
			cookieUtils.createAccessTokenCookie(httpServletResponse, jwt);
			cookieUtils.createRefreshTokenCookie(httpServletResponse, refreshToken.getToken());
		} else {
			cookieUtils.createAccessTokenCookie(httpServletResponse, jwt);
		}

		return ResponseEntity.ok().body(ResponseData.builder()
													.code("")
													.type(ResponseType.INFO)
													.message("Authentication successfull")
													.build());

	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterPayload payload) throws VtesException {
		User savedUser = userService.saveUser(payload);
		return ResponseEntity.ok()
							.body(ResponseData.builder()
							.code("").type(ResponseType.INFO)
							.message("Register successfull")
							.build());

	}

	@GetMapping("/refreshToken")
	public ResponseEntity<?> refreshtoken(HttpServletRequest request, HttpServletResponse httpServletResponse) {
		String requestRefreshToken = cookieUtils.getRefreshTokenFromCookie(request);
		return refreshTokenService.findByToken(requestRefreshToken).map(refreshTokenService::verifyExpiration)
				.map(RefreshToken::getUser).map(user -> {
					String token = jwtUtils.generateTokenFromEmail(user.getEmail());
					cookieUtils.createAccessTokenCookie(httpServletResponse, token);

					return ResponseEntity.ok().body(ResponseData.builder().type(ResponseType.INFO).code("")
							.message("Jwt Token recreated").build());

				})
				.orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
	}

	@GetMapping("/logout")
	public ResponseEntity<?> logoutUser(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		cookieUtils.deleteCookie(httpServletRequest, httpServletResponse, "accessToken");
		cookieUtils.deleteCookie(httpServletRequest, httpServletResponse, "refreshToken");
		Integer userId = userDetails.getId();
		refreshTokenService.deleteByUserId(userId);
		return ResponseEntity.ok()
				.body(ResponseData.builder().type(ResponseType.INFO).code("").message("Jwt Token deleted").build());

	}

}
