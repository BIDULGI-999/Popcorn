package com.bidulgi.common.security;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bidulgi.common.model.Role;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HeaderAuthFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain)
		throws ServletException, IOException {

		String userId = request.getHeader("X-User-Id");
		String roleKey = request.getHeader("X-User-Role");

		if (userId != null && roleKey != null) {
			try {
				List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleKey));

				Role role = Role.fromKey(roleKey);

				UserPrincipal principal = new UserPrincipal(UUID.fromString(userId), role);
				UsernamePasswordAuthenticationToken auth =
					new UsernamePasswordAuthenticationToken(principal, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (IllegalArgumentException e) {
				logger.warn("Invalid role key: " + roleKey, e);
			}
		}

		filterChain.doFilter(request, response);
	}
}