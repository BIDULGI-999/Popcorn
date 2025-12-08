package com.bidulgi.queueservice.infrastructure.redis;

import java.time.Instant;
import java.util.List;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

import com.bidulgi.queueservice.domain.model.QueueState;
import com.bidulgi.queueservice.domain.repository.QueueRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryAdapter implements QueueRepository {

	private final ReactiveRedisTemplate<String, String> redisTemplate;
	private final LuaScriptProvider luaScriptProvider;

	@Value("${queue.active-limit}")
	private int activeLimit;

	@Override
	public Mono<QueueState> enqueue(String userId, String productId) {
		String activeKey = generateActiveKey(productId);
		String waitingKey = generateWaitingKey(productId);
		return redisTemplate.execute(
			luaScriptProvider.getEnqueueScript(),
			List.of(activeKey, waitingKey),
			userId,
			String.valueOf(Instant.now().toEpochMilli()),
			String.valueOf(activeLimit)
		).single().map(QueueState::of);
	}

	private String generateActiveKey(String productId) {
		return "queue:active:" + productId;
	}

	private String generateWaitingKey(String productId) {
		return "queue:waiting:" + productId;
	}
}
