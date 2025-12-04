package com.bidulgi.userservice.domain.auth;

import java.time.Instant;

public interface TokenBlacklistRepository {

	void blacklist(String tokenId, Instant expiresAt);

	boolean isBlacklisted(String tokenId);
}
