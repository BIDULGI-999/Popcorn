package com.bidulgi.userservice.application.dto;

import com.bidulgi.userservice.domain.model.Gender;

public record UserUpdateRequest(
	String name,
	String nickname,
	String email,
	Gender gender,
	String password // null 이나 빈칸이면 변경 x
) {
}
