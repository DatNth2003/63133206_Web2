package com.ntd63133206.bookbuddy.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		var authourities = authentication.getAuthorities();
		var roles = authourities.stream().map(r -> r.getAuthority()).findFirst();
		
		roles.ifPresent(role -> System.out.println("CustomSuccessHandler: tìm thấy role " + authentication.getName() + role));
		if (roles.orElse("").equals("ROLE_ADMIN")) {
			response.sendRedirect("/admin-page");
		} else if (roles.orElse("").equals("ROLE_USER ")) {
			response.sendRedirect("/user-page");
		} else {
            System.out.println("CustomSuccessHandler: không tìm thấy role " + authentication.getName());
			response.sendRedirect("/error");
		}
		
		
		
	}

}
