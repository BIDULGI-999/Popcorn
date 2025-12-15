package com.bidulgi.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

	// HS256용 비밀키 (JWT SECRET에서 생성)
	private final SecretKey key;
	private final String issuer;

	private final long accessTokenValidityMillis = 30 * 60 * 1000L;          // 30분
	private final long refreshTokenValidityMillis = 7 * 24 * 60 * 60 * 1000L; // 7일

	public JwtTokenProvider(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.issuer}") String issuer
	) {
		byte[] keyBytes = Decoders.BASE64URL.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.issuer = issuer;
	}

	public GeneratedToken generateTokens(UUID userId, String role) {
		Instant now = Instant.now();

		String accessJti = UUID.randomUUID().toString();
		String refreshJti = UUID.randomUUID().toString();

		String accessToken = Jwts.builder()
			.issuer(issuer)
			.subject(userId.toString())
			.id(accessJti)
			.claim("role", role)
			.claim("type", "ACCESS")
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusMillis(accessTokenValidityMillis)))
			.signWith(key)
			.compact();

		String refreshToken = Jwts.builder()
			.issuer(issuer)
			.subject(userId.toString())
			.id(refreshJti)
			.claim("type", "REFRESH")
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusMillis(refreshTokenValidityMillis)))
			.signWith(key)
			.compact();

		return new GeneratedToken(
			accessToken,
			refreshToken,
			accessJti,
			refreshJti,
			now.plusMillis(accessTokenValidityMillis),
			now.plusMillis(refreshTokenValidityMillis)
		);
	}

	public Jws<Claims> parse(String token) {
		return Jwts.parser()
			.verifyWith(key)
			.requireIssuer(issuer)
			.build()
			.parseSignedClaims(token);
	}

	public String getTokenId(String token) {
		return parse(token).getPayload().getId();
	}

	public UUID getUserId(String token) {
		return UUID.fromString(parse(token).getPayload().getSubject());
	}

	public String getType(String token) {
		return parse(token).getPayload().get("type", String.class);
	}
}
