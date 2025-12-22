package com.bidulgi.productservice.infrastructure.client.dto;

import com.bidulgi.common.model.Role;

import java.util.UUID;

public record UserProfileResponse(
	UUID id,
	String name,
	String email,
	Gender gender,
	Integer age,
	Integer birthYear,
	Role role
) {
}
