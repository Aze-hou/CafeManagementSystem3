package com.inn.cafe.JWT;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private CustomerUsersDetailsService customerUsersDetailsService;
	@Autowired
	private JwtUtil jwtUtil;

	Claims claims = null;
	private String userName = null;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletReponse,
			FilterChain filterChain) throws ServletException, IOException {

//		Step 1: Allow Public Endpoints Without JWT Authentication
		if (httpServletRequest.getServletPath()
				.matches("/user/login|/user/forgotPassword|/user/signup|/user/resetPassword")) {
			filterChain.doFilter(httpServletRequest, httpServletReponse);
		} else {
//			Step 2: Extract Token from Request Header
			String authorizationHeader = httpServletRequest.getHeader("Authorization");
			String token = null;
// Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwiaWF0IjoxNjkwMTQ3MjAwLCJleHAiOjE2OTAxNTQ0MDB9.abc123def456ghi789jkl012mno345pqr678stu901vwx234yz

			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				token = authorizationHeader.substring(7);  // Remove "Bearer " prefix
				userName = jwtUtil.extractUserName(token);
				claims = jwtUtil.extractAllClaims(token);
//				✔ Retrieves the JWT token from the "Authorization" header.
//				✔ Extracts the username and claims from the token using JwtUtil.
			}
//			 Step 3: Validate the Token and Authenticate the User
			if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = customerUsersDetailsService.loadUserByUsername(userName);
				if (jwtUtil.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
//				✔ If a valid username exists and the user is not already authenticated:
//
//	Load user details from the database.
//	Validate the JWT token using jwtUtil.validateToken(token, userDetails).
//	Create an UsernamePasswordAuthenticationToken object and store it in Spring Security’s context (SecurityContextHolder).
			}
			filterChain.doFilter(httpServletRequest, httpServletReponse);
		}

	}

	public boolean isAdmin() {
		return "admin".equalsIgnoreCase((String) claims.get("role"));
	}

	public boolean isUser() {
		return "user".equalsIgnoreCase((String) claims.get("role"));
	}

	public String getCurrentUser() {
		return userName;
	}

}
