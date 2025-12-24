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

import com.bidulgi.gateway.auth.TokenBlacklistChecker;
import com.bidulgi.gateway.auth.UserPrincipal;
import com.bidulgi.gateway.jwt.JwtProvider;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

	private final JwtProvider jwtProvider;
	private final TokenBlacklistChecker blacklistChecker;

	private static final List<String> PREFIX_WHITELIST = List.of(
		"/v1/api/auth/login",
		"/v1/api/users",
		"/v1/products",
		"/actuator/health",
		"/v3/api-docs"
	);

	public JwtGlobalFilter(JwtProvider jwtProvider, TokenBlacklistChecker blacklistChecker) {
		this.jwtProvider = jwtProvider;
		this.blacklistChecker = blacklistChecker;
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
			log.info("아무거나");
			return exchange.getResponse().setComplete();
		}

		String jti = jwtProvider.getJti(token);

		// UserPrincipal principal = jwtProvider.getUserPrincipal(token);

		// ServerHttpRequest mutatedRequest = exchange.getRequest()
		// 	.mutate()
		// 	.header("X-User-Id", principal.id().toString())
		// 	.header("X-User-Role", principal.getRoleKey())
		// 	.build();

		// return chain.filter(exchange.mutate().request(mutatedRequest).build());

		return blacklistChecker.isBlacklisted(jti)
			.flatMap(blacklisted -> {
				if (blacklisted) {
					exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
					return exchange.getResponse().setComplete();
				}

				UserPrincipal principal = jwtProvider.getUserPrincipal(token);

				ServerHttpRequest mutatedRequest = exchange.getRequest()
					.mutate()
					.header("X-User-Id", principal.id().toString())
					.header("X-User-Role", principal.getRoleKey())
					.build();

				return chain.filter(exchange.mutate().request(mutatedRequest).build());
			});
	}

	private boolean isWhitelisted(String path) {
		return PREFIX_WHITELIST.stream()
			.anyMatch(path::contains);
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
