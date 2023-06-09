package com.vtes.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vtes.exception.AccessKeyExpiredException;
import com.vtes.exception.NotFoundException;
import com.vtes.exception.ParameterInvalidException;
import com.vtes.exception.VtesException;
import com.vtes.model.navitime.NavitimeExceptionMessage;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/*
 * @Author: chien.tranvan
 * @Date: 2023/05/26
 * @Description: Configuration class for Feign client.
 */

@Configuration
@Slf4j
public class FeignClientConfig {

	private final RapidAPIAccessKeyManager accessKeyManager;

	public FeignClientConfig(RapidAPIAccessKeyManager accessKeyManager) {
		this.accessKeyManager = accessKeyManager;
	}

	@Bean
	public RequestInterceptor requestInterceptor() {
		return new FeignClientInterceptor(accessKeyManager);
	}

	@Bean
	public ErrorDecoder navitimeErrorDecoder() {
		return new NavitimeErrorDecoder(accessKeyManager);
	}

	// Interceptor for adding the API key to the request header
	public static class FeignClientInterceptor implements RequestInterceptor {

		private final RapidAPIAccessKeyManager accessKeyManager;

		public FeignClientInterceptor(RapidAPIAccessKeyManager accessKeyManager) {
			this.accessKeyManager = accessKeyManager;
		}

		@Override
		public void apply(RequestTemplate requestTemplate) {
			String apiKey = accessKeyManager.getCurrentAccessKey();
			requestTemplate.header("X-RapidAPI-Key", apiKey);
		}
	}

	// Error decoder for handling API response errors and rotate access key when
	// current key expired
	public static class NavitimeErrorDecoder implements ErrorDecoder {
		private ErrorDecoder errorDecoder = new Default();
		public final RapidAPIAccessKeyManager accessKeyManager;
		private static final Integer TOO_MANY_REQUEST = 429;

		public NavitimeErrorDecoder(RapidAPIAccessKeyManager accessKeyManager) {
			this.accessKeyManager = accessKeyManager;
		}

		@Override
		public Exception decode(String methodKey, Response response) {
			NavitimeExceptionMessage message = null;
			try (InputStream bodyIs = response.body()
		            .asInputStream()) {
		            ObjectMapper mapper = new ObjectMapper();
		            message = mapper.readValue(bodyIs, NavitimeExceptionMessage.class);
		        } catch (IOException e) {
		            return new Exception(e.getMessage());
		        }
			
			if (response.status() == TOO_MANY_REQUEST) {
				String currentKey = accessKeyManager.getCurrentAccessKey();
				String keyIndex = accessKeyManager.getKeyIndex();
				
				accessKeyManager.rotateAccesskey();
				log.info("Current access key index = {} , key = {}",keyIndex,currentKey);
				return new AccessKeyExpiredException(currentKey);
			}
			if(message.getStatus_code() == 500) {
				return new VtesException("API_ER04","This specifed routes not found");
			}
			if(message.getStatus_code() == 404) {
				return new NotFoundException(message.getMessage());
			}
			if(message.getStatus_code() == 400) {
				return new ParameterInvalidException("Bad Request");
			}

			 return errorDecoder.decode(methodKey, response);
		}
	}
}
