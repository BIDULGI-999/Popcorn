package com.bidulgi.userservice.infrastructure.auth;

import com.bidulgi.userservice.domain.auth.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class RedisTokenBlacklistRepository implements TokenBlacklistRepository {

	private final StringRedisTemplate redis;

	@Override
	public void blacklist(String tokenId, Instant expiresAt) {
		String key = "bl:" + tokenId;
		long ttl = Duration.between(Instant.now(), expiresAt).toSeconds();

		redis.opsForValue().set(key, "1", Duration.ofSeconds(ttl));
	}

	@Override
	public boolean isBlacklisted(String tokenId) {
		String key = "bl:" + tokenId;
		return redis.hasKey(key);
	}
}
