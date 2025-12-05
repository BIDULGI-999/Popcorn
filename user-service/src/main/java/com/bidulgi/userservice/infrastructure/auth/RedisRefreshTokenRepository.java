package com.bidulgi.userservice.infrastructure.auth;

import com.bidulgi.userservice.domain.auth.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

	private final StringRedisTemplate redis;

	private String key(UUID userId) {
		return "refresh:" + userId.toString();
	}

	@Override
	public void save(UUID userId, String refreshToken, Instant expiresAt) {
		String k = key(userId);
		redis.opsForHash().put(k, refreshToken, expiresAt.toString());
	}

	@Override
	public Optional<String> findValidToken(UUID userId, String refreshToken) {
		String k = key(userId);

		Object expiresAtObj = redis.opsForHash().get(k, refreshToken);
		if (expiresAtObj == null) return Optional.empty();

		Instant expiresAt = Instant.parse(expiresAtObj.toString());
		if (expiresAt.isBefore(Instant.now())) {
			// 만료 → 삭제
			redis.opsForHash().delete(k, refreshToken);
			return Optional.empty();
		}

		return Optional.of(refreshToken);
	}

	@Override
	public void delete(UUID userId, String refreshToken) {
		redis.opsForHash().delete(key(userId), refreshToken);
	}

	@Override
	public void deleteAllByUserId(UUID userId) {
		redis.delete(key(userId));
	}
}
