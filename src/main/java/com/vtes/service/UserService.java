package com.vtes.service;

import com.vtes.entity.User;
import com.vtes.exception.VtesException;
import com.vtes.payload.EmailPayload;
import com.vtes.payload.ResetPasswordPayload;
import com.vtes.payload.UpdateUserPayload;
import com.vtes.security.service.UserDetailsImpl;

public interface UserService {
	User getUser(String email);

	void activeUser(String token) throws VtesException;

	User updateUser(UpdateUserPayload updateInfoRequest, UserDetailsImpl userDetailsImpl) throws VtesException;

	void sendResetPasswordViaEmail(EmailPayload passwordResetEmailRequest) throws VtesException;

	void resetPassword(ResetPasswordPayload passwordResetRequest) throws VtesException;
}
