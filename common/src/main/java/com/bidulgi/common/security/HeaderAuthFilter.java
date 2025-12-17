package com.bidulgi.common.security;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bidulgi.common.auth.JwtTokenProvider;
import com.bidulgi.common.model.Role;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HeaderAuthFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	public HeaderAuthFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain)
		throws ServletException, IOException {

		// 1) Gateway가 내려준 헤더 기반 인증(우선)
		String userId = request.getHeader("X-User-Id");
		String roleKey = request.getHeader("X-User-Role");

		if (StringUtils.hasText(userId) && StringUtils.hasText(roleKey)) {
			try {
				authenticate(UUID.fromString(userId), roleKey);
				filterChain.doFilter(request, response);
				return;
			} catch (IllegalArgumentException e) {
				logger.warn("Invalid X-User headers. userId=" + userId + ", role=" + roleKey, e);
				// fall through to JWT
			}
		}

		// 2) Authorization Bearer JWT fallback (Swagger/직접 호출)
		String authHeader = request.getHeader("Authorization");
		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			if (jwtTokenProvider.validate(token) && jwtTokenProvider.isAccessToken(token)) {
				UUID uid = jwtTokenProvider.getUserId(token);
				String jwtRoleKey = jwtTokenProvider.getRole(token); // 예: ROLE_CUSTOMER

				authenticate(uid, jwtRoleKey);
			}
		}

		filterChain.doFilter(request, response);
	}

	private void authenticate(UUID userId, String roleKey) {
		// roleKey 자체가 ROLE_CUSTOMER 형태이므로 그대로 authority로 사용
		List<SimpleGrantedAuthority> authorities =
			List.of(new SimpleGrantedAuthority(roleKey));

		Role role = Role.fromKey(roleKey);
		UserPrincipal principal = new UserPrincipal(userId, role);

		UsernamePasswordAuthenticationToken auth =
			new UsernamePasswordAuthenticationToken(principal, null, authorities);

		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}
