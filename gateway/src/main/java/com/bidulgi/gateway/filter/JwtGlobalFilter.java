package com.bidulgi.gateway.filter;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.gateway.jwt.JwtProvider;

import reactor.core.publisher.Mono;

@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

	private final JwtProvider jwtProvider;

	private static final List<String> PREFIX_WHITELIST = List.of(
		"/v1/users/login",
		"/v1/users/signup",
		"/v1/products",
		"actuator/health"
	);

	private static final List<String> AUTH_REQUIRED_PATHS = List.of(
		"/likes",
		"/favorite",
		"/admin"
	);

	public JwtGlobalFilter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String path = exchange.getRequest().getURI().getPath();

		if (isWhitelisted(path)) {
			return chain.filter(exchange);
		}

		String token = resolveToken(exchange);

		if (token == null || !jwtProvider.validateToken(token)) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		UserPrincipal principal = jwtProvider.getUserPrincipal(token);

		ServerHttpRequest mutatedRequest = exchange.getRequest()
			.mutate()
			.header("X-User-Id", principal.id().toString())
			.header("X-User-Email", principal.email())
			.header("X-User-Role", principal.getRoleKey())
			.build();

		return chain.filter(exchange.mutate().request(mutatedRequest).build());
	}

	private boolean isWhitelisted(String path) {

		boolean prefixMatch = PREFIX_WHITELIST.stream()
			.anyMatch(path::startsWith);

		if (!prefixMatch) return false;

		// 좋아요/찜 같은 인증 필요한 경로는 제외
		boolean requiresAuth = AUTH_REQUIRED_PATHS.stream()
			.anyMatch(path::endsWith)
			|| AUTH_REQUIRED_PATHS.stream()
			.anyMatch(path::contains);

		return !requiresAuth;
	}

	private String resolveToken(ServerWebExchange exchange) {
		String authHeader = exchange.getRequest()
			.getHeaders()
			.getFirst(HttpHeaders.AUTHORIZATION);

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}

	@Override
	// 인증은 최대한 빠르게 진행되야 함
	public int getOrder() {
		return -1;
	}
}
