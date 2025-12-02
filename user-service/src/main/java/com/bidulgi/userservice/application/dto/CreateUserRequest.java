package com.bidulgi.userservice.application.dto;

import java.time.LocalDate;

import com.bidulgi.common.model.Role;
import com.bidulgi.userservice.domain.model.Gender;

public record CreateUserRequest(
	String name,
	String nickname,
	String email,
	String password, // 암호화전
	LocalDate birthday,
	Gender gender,
	Role role
) {
}
