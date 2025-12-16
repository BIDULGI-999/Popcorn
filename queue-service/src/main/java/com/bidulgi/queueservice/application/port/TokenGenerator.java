package com.bidulgi.queueservice.application.port;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface TokenGenerator {

	Mono<String> createAccessToken(UUID userId, UUID productId);
}
