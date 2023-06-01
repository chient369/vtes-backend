package com.vtes.exception;

public class AccessKeyExpiredException extends Exception {

		private static final long serialVersionUID = 1L;

		public AccessKeyExpiredException(String key) {
			super(String.format("Current key is expried [%s]", key));
	}
		

}
