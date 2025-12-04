package com.bidulgi.userservice.domain.auth;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {

	void save(UUID userId, String refreshToken, Instant expireAt);

	Optional<String> findValidToken(UUID userId, String refreshToken);

	void delete(UUID userId, String refreshToken);

	void deleteAllByUserId(UUID userId);
}
