package com.bidulgi.common.auth;

import java.time.Instant;

public record GeneratedToken(
	String accessToken,
	String refreshToken,
	String accessTokenId,
	String refreshTokenId,
	Instant accessTokenExpiresAt,
	Instant refreshTokenExpiresAt
) {}
