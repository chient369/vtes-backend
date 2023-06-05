package com.vtes.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vtes.model.ResponseData;
import com.vtes.model.ResponseData.ResponseType;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		ResponseData responseData = ResponseData.builder()
				.type(ResponseType.ERROR)
				.message(authException.getMessage())
				.code("").build();

		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), responseData);
	}

}
