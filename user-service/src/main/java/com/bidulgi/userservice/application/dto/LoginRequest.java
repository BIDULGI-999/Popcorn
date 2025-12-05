package com.bidulgi.userservice.application.dto;

public record LoginRequest(
	String email,
	String password
) {
}
