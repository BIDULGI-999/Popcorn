package com.bidulgi.queueservice.infrastructure.config;

import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.bidulgi.common.model.Role;
import com.bidulgi.common.security.UserPrincipal;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
		String userRole = exchange.getRequest().getHeaders().getFirst("X-User-Role");

		if (userId == null || userRole == null) {
			return chain.filter(exchange);
		}

		UserPrincipal principal = new UserPrincipal(UUID.fromString(userId), Role.fromKey(userRole));

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
			principal,
			null,
			List.of(new SimpleGrantedAuthority(userRole))
		);

		return chain.filter(exchange)
			.contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
	}
}
