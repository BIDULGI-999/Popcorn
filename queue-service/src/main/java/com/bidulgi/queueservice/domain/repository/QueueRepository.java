package com.bidulgi.queueservice.domain.repository;

import com.bidulgi.queueservice.domain.vo.QueueState;

import reactor.core.publisher.Mono;

public interface QueueRepository {

	Mono<QueueState> enqueue(String userId, String productId);

	Mono<String> dequeue(String userId, String productId);

}
