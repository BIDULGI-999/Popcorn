package com.bidulgi.gateway.jwt;

import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.bidulgi.common.model.Role;
import com.bidulgi.common.security.UserPrincipal;

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
		byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
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

		String idStr = claims.get("id", String.class);
		String email = claims.get("email", String.class);
		String roleKey = claims.get("role", String.class);

		Role role = Role.fromKey(roleKey);

		return new UserPrincipal(UUID.fromString(idStr), email, role);
	}

	private Claims parseClaims(String token) {
		Jws<Claims> jws = Jwts.parser()
			.verifyWith(key)
			.requireIssuer(issuer)
			.build()
			.parseSignedClaims(token);

		return jws.getPayload();
	}
}
