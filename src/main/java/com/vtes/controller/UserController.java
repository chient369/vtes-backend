package com.vtes.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vtes.entity.User;
import com.vtes.payload.request.PasswordResetEmailRequest;
import com.vtes.payload.request.PasswordResetRequest;
import com.vtes.payload.request.UpdateInfoRequest;
import com.vtes.payload.request.UserActiveRequest;
import com.vtes.payload.response.ResponseData;
import com.vtes.payload.response.ResponseData.ResponseType;
import com.vtes.payload.response.UserResponse;
import com.vtes.repository.DepartmentRepository;
import com.vtes.repository.UserRepository;
import com.vtes.security.services.UserDetailsImpl;
import com.vtes.service.EmailService;
import com.vtes.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping()
	public ResponseEntity<?> getUser() {

		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		User user = userService.getUser(userDetails.getEmail());

		UserResponse userRespons = modelMapper.map(user, new TypeToken<UserResponse>() {
		}.getType());
		return ResponseEntity.ok().body(
				ResponseData.builder().type(ResponseType.INFO).code("").message("Success").data(userRespons).build());
	}

	@PostMapping("/active")
	public ResponseEntity<?> activeUser(@Valid @RequestBody UserActiveRequest userActiveRequest) {

		return userService.activeUser(userActiveRequest.getVerifyCode());
	}

	@PostMapping("/emails")
	public ResponseEntity<?> sendResetPasswordViaEmail(
			@RequestBody PasswordResetEmailRequest passwordResetEmailRequest) {

		return userService.sendResetPasswordViaEmail(passwordResetEmailRequest);

	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
		return userService.resetPassword(passwordResetRequest);

	}

	@PutMapping()
	public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateInfoRequest updateInfoRequest) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		return userService.updateUser(updateInfoRequest, userDetails);
	}
}
