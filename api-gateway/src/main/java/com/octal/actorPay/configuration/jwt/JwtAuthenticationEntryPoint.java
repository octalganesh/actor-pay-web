package com.octal.actorPay.configuration.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author - Naveen
 *  It is the entry point to check if a user is authenticated and logs the person in or throws exception (unauthorized)
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
						 AuthenticationException authException) throws IOException, ServletException {
		System.out.println("Jwt authentication failed:" + authException);

		response.sendError(HttpServletResponse.SC_UNAUTHORIZED	, "Jwt authentication failed");
	}
}