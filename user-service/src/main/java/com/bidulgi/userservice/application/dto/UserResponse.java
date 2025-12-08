package com.bidulgi.userservice.application.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.bidulgi.common.model.Role;
import com.bidulgi.userservice.domain.model.Gender;

public record UserResponse(
	UUID id,
	String name,
	String nickname,
	String email,
	LocalDate birthday,
	Gender gender,
	Role role
) {
}
