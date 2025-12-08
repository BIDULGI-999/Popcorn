package com.bidulgi.queueservice.infrastructure.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

import com.bidulgi.queueservice.domain.repository.TokenRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryAdapter implements TokenRepository {

	private final ReactiveRedisTemplate<String, String> redisTemplate;

	@Value("${jwt.ttl-minutes}")
	private Duration ttl;

	@Override
	public Mono<String> findToken(String userId, String productId) {
		String key = generateTokenKey(userId, productId);
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public Mono<Boolean> saveToken(String userId, String productId, String token) {
		String key = generateTokenKey(userId, productId);
		return redisTemplate.opsForValue().set(key, token, ttl);
	}

	private String generateTokenKey(String userId, String productId) {
		return "token:" + productId + ":" + userId;
	}


}
