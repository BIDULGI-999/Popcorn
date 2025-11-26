package com.bidulgi.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	MASTER("ROLE_MASTER", "마스터 관리자"),
	MANAGER("ROLE_MANAGER", "팝업스토어 관리자"),
	CUSTOMER("ROLE_CUSTOMER", "일반 회원");

	private final String key;
	private final String description;

	public static Role fromKey(String key) {
		for (Role role : values()) {
			if (role.key.equals(key)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown role key: " + key);
	}
}
