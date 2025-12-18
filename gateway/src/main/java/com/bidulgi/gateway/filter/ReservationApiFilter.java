package com.bidulgi.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.bidulgi.gateway.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReservationApiFilter implements GlobalFilter, Ordered {

	private static final String RESERVATION_API_PREFIX = "/internal/reservations";
	private static final String HEADER_ACCESS_TOKEN = "X-Access-Token";
	private static final String HEADER_USER_ID = "X-User-Id";

	private final JwtProvider jwtProvider;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();

		if (!path.contains(RESERVATION_API_PREFIX)) {
			return chain.filter(exchange);
		}

		String accessToken = extractHeader(exchange, HEADER_ACCESS_TOKEN);
		if (!isValidToken(accessToken)) {
			return unauthorized(exchange);
		}

		String requestUserId = extractHeader(exchange, HEADER_USER_ID);
		String tokenUserId = jwtProvider.getUserId(accessToken);

		if (!isUserIdMatched(requestUserId, tokenUserId)) {
			return unauthorized(exchange);
		}

		return chain.filter(exchange);
	}

	private String extractHeader(ServerWebExchange exchange, String headerName) {
		return exchange.getRequest().getHeaders().getFirst(headerName);
	}

	private boolean isValidToken(String token) {
		return token != null && jwtProvider.validateToken(token);
	}

	private boolean isUserIdMatched(String requestUserId, String tokenUserId) {
		return tokenUserId != null && tokenUserId.equals(requestUserId);
	}

	private Mono<Void> unauthorized(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}

	@Override
	public int getOrder() {
		return 0; // 사용자 인증 후 실행되도록 우선순위 설정
	}
}