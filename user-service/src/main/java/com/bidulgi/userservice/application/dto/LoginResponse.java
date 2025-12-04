package com.bidulgi.userservice.application.dto;

public record LoginResponse(
	String accessToken,
	String refreshToken,
	String tokenType
) {}
