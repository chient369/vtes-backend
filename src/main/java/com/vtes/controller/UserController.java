package com.vtes.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vtes.entity.User;
import com.vtes.exception.VtesException;
import com.vtes.model.ResponseData;
import com.vtes.model.ResponseData.ResponseType;
import com.vtes.model.UserDTO;
import com.vtes.payload.EmailPayload;
import com.vtes.payload.ResetPasswordPayload;
import com.vtes.payload.UpdateUserPayload;
import com.vtes.payload.UserActiveTokenPayload;
import com.vtes.security.services.UserDetailsImpl;
import com.vtes.service.UserServiceImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping()
	public ResponseEntity<?> getUser() {

		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		User user = userService.getUser(userDetails.getEmail());

		UserDTO userRespons = modelMapper.map(user, new TypeToken<UserDTO>() {
		}.getType());
		return ResponseEntity.ok().body(
				ResponseData.builder()
				.type(ResponseType.INFO)
				.code("")
				.message("Success")
				.data(userRespons)
				.build());
	}

	@PostMapping("/active")
	public ResponseEntity<?> activeUser(@Valid @RequestBody UserActiveTokenPayload payload) throws VtesException {
		userService.activeUser(payload.getVerifyCode());
		
		return ResponseEntity.ok().body(
				ResponseData.builder()
				.type(ResponseType.INFO)
				.code("")
				.message("Account verify successfully")
				.build());
	}

	@PostMapping("/emails")
	public ResponseEntity<?> sendResetPasswordViaEmail(@RequestBody EmailPayload passwordResetEmailRequest) throws VtesException {
		userService.sendResetPasswordViaEmail(passwordResetEmailRequest);
		
		
		return ResponseEntity.ok()
				.body(ResponseData.builder()
						.type(ResponseType.INFO)
						.code("")
						.message("Verify mail has sent")
						.build());

	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordPayload passwordResetRequest) throws VtesException {
		userService.resetPassword(passwordResetRequest);
		
		return ResponseEntity.ok()
						.body(ResponseData.builder()
						.type(ResponseType.INFO).
						code("")
						.message("Reset password successfully!")
						.build());

	}

	@PutMapping()
	public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserPayload updateInfoRequest) throws VtesException {
	 userService.updateUser(updateInfoRequest, getUserDetails());
		return ResponseEntity.ok()
							.body(ResponseData.builder()
							.type(ResponseType.INFO)
							.code("200")
							.message("Update successfull")
							.build());
	}
	
	private UserDetailsImpl getUserDetails() {
		return (UserDetailsImpl) SecurityContextHolder
									.getContext()
									.getAuthentication()
									.getPrincipal();
	}
}
