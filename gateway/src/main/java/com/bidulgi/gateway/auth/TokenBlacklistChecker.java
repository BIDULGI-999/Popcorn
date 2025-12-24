package com.bidulgi.gateway.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TokenBlacklistChecker {

	private final ReactiveStringRedisTemplate redis;

	public Mono<Boolean> isBlacklisted(String jti) {
		return redis.hasKey("bl:" + jti);
	}
}
