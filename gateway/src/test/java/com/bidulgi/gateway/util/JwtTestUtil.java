package com.bidulgi.gateway.util;

import io.jsonwebtoken.io.Decoders;

public class JwtTestUtil {
	public static void main(String[] args) {
		String secret = System.getenv("JWT_SECRET"); // 환경변수에서 읽기
		String issuer = "bidulgi";

		if (secret == null) {
			System.err.println("⚠ JWT_SECRET 환경변수가 없습니다.");
			return;
		}

		byte[] keyBytes = Decoders.BASE64URL.decode(secret);
		javax.crypto.SecretKey key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);

		String token = io.jsonwebtoken.Jwts.builder()
			.issuer(issuer)
			.claim("id", "8b87a0f0-2f6b-4e2a-8a30-1234567890ab")
			.claim("email", "test@bidulgi.com")
			.claim("role", "ROLE_USER")
			.signWith(key)
			.compact();

		System.out.println("TEST TOKEN: " + token);
	}
}
