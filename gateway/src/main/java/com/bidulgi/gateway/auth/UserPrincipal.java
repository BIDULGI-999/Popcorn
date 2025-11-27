package com.bidulgi.gateway.auth;

import java.util.UUID;


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