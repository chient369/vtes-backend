package com.vtes.service;

import org.springframework.http.ResponseEntity;

import com.vtes.entity.User;
import com.vtes.payload.request.PasswordResetEmailRequest;
import com.vtes.payload.request.PasswordResetRequest;
import com.vtes.payload.request.UpdateInfoRequest;
import com.vtes.security.services.UserDetailsImpl;

public interface UserService {
	User getUser(String email);

	ResponseEntity<?> activeUser(String token);

	ResponseEntity<?> updateUser(UpdateInfoRequest updateInfoRequest, UserDetailsImpl userDetailsImpl);

	ResponseEntity<?> sendResetPasswordViaEmail(PasswordResetEmailRequest passwordResetEmailRequest);

	ResponseEntity<?> resetPassword(PasswordResetRequest passwordResetRequest);
}
