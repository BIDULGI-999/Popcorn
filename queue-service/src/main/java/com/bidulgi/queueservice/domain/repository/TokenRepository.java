package com.bidulgi.queueservice.domain.repository;

import reactor.core.publisher.Mono;

public interface TokenRepository {

	Mono<String> findToken(String userId, String productId);

	Mono<Boolean> saveToken(String userId, String productId, String token);
}
