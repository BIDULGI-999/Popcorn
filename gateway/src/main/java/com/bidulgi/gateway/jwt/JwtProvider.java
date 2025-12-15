package com.bidulgi.gateway.jwt;

import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.bidulgi.gateway.auth.Role;
import com.bidulgi.gateway.auth.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	private final SecretKey key;
	private final String issuer;

	public JwtProvider(JwtProperties properties) {
		byte[] keyBytes = Decoders.BASE64URL.decode(properties.getSecret());
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.issuer = properties.getIssuer();
	}

	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public UserPrincipal getUserPrincipal(String token) {
		Claims claims = parseClaims(token);

		UUID userId = UUID.fromString(claims.getSubject());
		String roleKey = claims.get("role", String.class);

		Role role = Role.fromKey(roleKey);

		return new UserPrincipal(userId, role);
	}

	private Claims parseClaims(String token) {
		Jws<Claims> jws = Jwts.parser()
			.verifyWith(key)
			.requireIssuer(issuer)
			.build()
			.parseSignedClaims(token);

		return jws.getPayload();
	}

	public String getJti(String token) {
		return parseClaims(token).getId();   // 표준 jti
	}

}
