package com.bidulgi.queueservice.application.port;

import reactor.core.publisher.Mono;

public interface TokenGenerator {

	Mono<String> createAccessToken(String userId, String productId);
}
