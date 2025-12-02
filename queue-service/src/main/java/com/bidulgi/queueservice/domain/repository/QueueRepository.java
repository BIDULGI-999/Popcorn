package com.bidulgi.queueservice.domain.repository;

import com.bidulgi.queueservice.domain.model.QueueState;

import reactor.core.publisher.Mono;

public interface QueueRepository {

	Mono<QueueState> enqueue(String userId, String productId);

}
