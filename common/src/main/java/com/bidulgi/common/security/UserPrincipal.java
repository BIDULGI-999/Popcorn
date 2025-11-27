package com.bidulgi.common.security;

import java.util.UUID;

import com.bidulgi.common.model.Role;

public record UserPrincipal(
	UUID id,
	Role role
) {

	public boolean isMaster() {
		return this.role == Role.MASTER;
	}

	public boolean isManager() {
		return this.role == Role.MANAGER;
	}

	public boolean hasRole(Role targetRole) {
		return this.role == targetRole;
	}

	public String getRoleKey() {
		return this.role.getKey();
	}
}