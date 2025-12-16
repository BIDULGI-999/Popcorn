package com.bidulgi.queueservice.infrastructure.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bidulgi.queueservice.application.port.TokenGenerator;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtProvider implements TokenGenerator {

	private final SecretKey secretKey;

	private static final String ISSUER = "bidulgi";

	@Value("${jwt.ttl-minutes}")
	private Duration ttl;

	public JwtProvider(@Value("${jwt.secret}") String secretKey) {
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
	}

	@Override
	public Mono<String> createAccessToken(UUID userId, UUID productId) {
		Instant now = Instant.now();
		String token = Jwts.builder()
			.claim("id", userId)
			.claim("productId", productId)
			.issuer(ISSUER)
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plus(ttl)))
			.signWith(secretKey)
			.compact();

		return Mono.just(token);
	}
}
