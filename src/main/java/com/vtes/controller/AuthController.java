package com.vtes.controller;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vtes.entity.Department;
import com.vtes.entity.RefreshToken;
import com.vtes.entity.User;
import com.vtes.exception.AuthenticationFailedException;
import com.vtes.exception.TokenRefreshException;
import com.vtes.payload.request.LoginRequest;
import com.vtes.payload.request.SignupRequest;
import com.vtes.payload.response.ResponseData;
import com.vtes.payload.response.ResponseData.ResponseType;
import com.vtes.repository.DepartmentRepository;
import com.vtes.repository.UserRepository;
import com.vtes.security.jwt.CookieUtils;
import com.vtes.security.jwt.JwtUtils;
import com.vtes.security.services.RefreshTokenService;
import com.vtes.security.services.UserDetailsImpl;
import com.vtes.service.EmailService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	CookieUtils cookieUtils;

	@Autowired
	RefreshTokenService refreshTokenService;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
			HttpServletResponse httpServletResponse) throws AuthenticationFailedException {

		Authentication authentication = null;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		} catch (Exception e) {
			throw new AuthenticationFailedException(loginRequest.getEmail());
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		User user = new User();
		user = userRepository.findById(userDetails.getId()).get();
		if (user.getStatus() == 0) {
			return ResponseEntity.badRequest()
					.body(ResponseData.builder()
							.code("API001_ER02")
							.type(ResponseType.ERROR)
							.message("This account is not active yet").build());
		}

		String jwt = jwtUtils.generateJwtToken(userDetails);

		if (loginRequest.isRemember()) {
			RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
			cookieUtils.createAccessTokenCookie(httpServletResponse, jwt);
			cookieUtils.createRefreshTokenCookie(httpServletResponse, refreshToken.getToken());
		} else {
			cookieUtils.createAccessTokenCookie(httpServletResponse, jwt);
		}

		return ResponseEntity.ok().body(
				ResponseData.builder()
					.code("").
					type(ResponseType.INFO)
					.message("Authentication successfull")
					.build());

	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest()
					.body(ResponseData.builder()
							.code("API002_ER")
							.type(ResponseType.ERROR)
							.message("This email has already been used")
							.build());
		}

		if (departmentRepository.findById(signUpRequest.getDepartmentId()).isEmpty()) {
			return ResponseEntity.badRequest()
					.body(ResponseData.builder()
							.code("API_ER04")
							.type(ResponseType.ERROR)
							.message("Department ID is required")
							.build());
		}

		Department department = new Department();
		department = departmentRepository.findById(signUpRequest.getDepartmentId()).get();

		User user = new User(signUpRequest.getFullName(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()), department);
		user.setStatus((short) 0);
		String tokenActive = jwtUtils.generateTokenToActiveUser(signUpRequest.getEmail());
		user.setVerifyCode(tokenActive);
		user.setCreateDt(Instant.now());
		user.setDeleteFlag(false);
		userRepository.save(user);
		emailService.sendRegistrationUserConfirm(signUpRequest.getEmail(), tokenActive);

		return ResponseEntity.ok()
				.body(ResponseData.builder()
						.code("")
						.type(ResponseType.INFO)
						.message("Register successfull").build());

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
