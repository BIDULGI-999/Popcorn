package com.bidulgi.queueservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebFluxSecurity
public class SecurityConfig {

	private final AuthenticationFilter authenticationFilter;

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
			.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
			.addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
			.authorizeExchange(exchanges ->
				exchanges.pathMatchers(
					"/swagger-ui/**",
					"/actuator/**",
					"/v3/api-docs/**",
					"/health/**",
					"/internal/**"
					).permitAll()
				.anyExchange().authenticated()
			)
			.build();
	}
}
