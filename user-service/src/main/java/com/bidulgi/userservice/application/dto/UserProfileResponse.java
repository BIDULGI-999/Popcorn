package com.bidulgi.userservice.application.dto;

import com.bidulgi.common.model.Role;
import com.bidulgi.userservice.domain.model.Gender;
import com.bidulgi.userservice.domain.model.User;

import java.time.LocalDate;
import java.time.Period;
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

	public static UserProfileResponse from(User user) {
		Integer age = null;
		Integer birthYear = null;
		LocalDate birthday = user.getBirthday(); // birthday를 LocalDate로 사용한다고 가정

		if (birthday != null) {
			birthYear = birthday.getYear();
			age = Period.between(birthday, LocalDate.now()).getYears();
		}

		return new UserProfileResponse(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getGender(),
			age,
			birthYear,
			user.getRole()
		);
	}
}
