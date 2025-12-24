package com.bidulgi.productservice.infrastructure.client.dto;

import java.util.UUID;

import com.bidulgi.common.model.Role;

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
