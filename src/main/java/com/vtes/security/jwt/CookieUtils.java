package com.vtes.security.jwt;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

	@Value("${vtes.app.accessCookieExpiration}")
	private int accessCookieExpiration;

	@Value("${vtes.app.refreshCookieExpiration}")
	private int refreshCookieExpiration;

	public void createCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly,
			boolean secure, String path) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setHttpOnly(httpOnly);
		cookie.setSecure(secure);
		cookie.setPath(path);
		response.addCookie(cookie);
	}

	public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					cookie.setValue(null);
					cookie.setPath("/");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
					break;
				}
			}
		}
	}

	public void createAccessTokenCookie(HttpServletResponse response, String accessToken) {
		createCookie(response, "accessToken", accessToken, accessCookieExpiration, true, false, "/");
	}

	public void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		createCookie(response, "refreshToken", refreshToken, refreshCookieExpiration, true, false, "/");
	}
	public String getRefreshTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refreshToken")) {
					String accessToken = cookie.getValue();
					return accessToken;
				}
			}
		}
		return null;
	}
}
