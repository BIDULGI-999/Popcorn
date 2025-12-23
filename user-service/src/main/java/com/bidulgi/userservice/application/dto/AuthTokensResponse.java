package com.bidulgi.userservice.application.dto;

public record AuthTokensResponse(
	String accessToken,
	String refreshToken,
	String tokenType
) {
	public static AuthTokensResponse of(String accessToken, String refreshToken) {
		return new AuthTokensResponse(accessToken, refreshToken, "Bearer");
	}
}
