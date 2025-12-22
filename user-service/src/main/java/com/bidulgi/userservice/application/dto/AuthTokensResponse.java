package com.bidulgi.userservice.application.dto;

public record AuthTokensResponse(
	String accessToken,
	String refreshToken
) {
	public static AuthTokensResponse of(String accessToken, String refreshToken) {
		return new AuthTokensResponse(accessToken, refreshToken);
	}
}
