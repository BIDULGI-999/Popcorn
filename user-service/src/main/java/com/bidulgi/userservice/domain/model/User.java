package com.bidulgi.userservice.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import com.bidulgi.common.model.BaseEntity;
import com.bidulgi.common.model.Role;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@AttributeOverride(
	name = "createdBy",
	column = @Column(name = "created_by", nullable = true)
)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@Column(nullable = false, length = 50)
	private String name;

	private String nickname;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false)
	private String password;

	private LocalDate birthday;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	private Role role;

	public static User create(String name,
		String nickname,
		String email,
		String password,
		LocalDate birthday,
		Gender gender,
		Role role) {
		return User.builder()
			.name(name)
			.nickname(nickname)
			.email(email)
			.password(password)
			.birthday(birthday)
			.gender(gender)
			.role(role)
			.build();
	}

	public void updateProfile(String name,
		String nickname,
		String email,
		Gender gender) {
		this.name = name;
		this.nickname = nickname;
		this.gender = gender;
	}

	public void updatePassword(String password) {
		this.password = password;
	}
}
