package com.bidulgi.queueservice.infrastructure.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.bidulgi.queueservice.domain.vo.QueueState;
import com.bidulgi.queueservice.domain.repository.QueueRepository;
import com.bidulgi.queueservice.infrastructure.redis.LuaScriptProvider;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryAdapter implements QueueRepository {

	private final ReactiveRedisTemplate<String, String> redisTemplate;
	private final LuaScriptProvider luaScriptProvider;

	private static final String ACTIVE_KEY = "queue:active";
	private static final String WAITING_PRODUCTS_KEY = "queue:waiting:products";

	@Value("${queue.active-limit}")
	private int activeLimit;

	@Override
	public Mono<QueueState> enqueue(String userId, String productId) {
		String waitingKey = generateWaitingKey(productId);
		return redisTemplate.execute(
			luaScriptProvider.getEnqueueScript(),
			List.of(ACTIVE_KEY, waitingKey, WAITING_PRODUCTS_KEY),
			userId,
			String.valueOf(Instant.now().toEpochMilli()),
			String.valueOf(activeLimit),
			productId
		).single().map(QueueState::of);
	}

	@Override
	public Mono<String> dequeue(String userId, String productId) {
		String valueToRemove = productId + ":" + userId;
		return redisTemplate.execute(
			luaScriptProvider.getDequeueScript(),
			List.of(ACTIVE_KEY, WAITING_PRODUCTS_KEY),
			valueToRemove
		).singleOrEmpty()
			.filter(StringUtils::hasText);
	}

	@Override
	public Mono<Long> getPosition(String userId, String productId) {
		String waitingKey = generateWaitingKey(productId);
		return redisTemplate.opsForZSet()
			.rank(waitingKey, userId)
			.map(index -> index + 1);
	}

	@Override
	public Mono<Boolean> remove(String userId, String productId) {
		String waitingKey = generateWaitingKey(productId);
		return redisTemplate.opsForZSet()
			.remove(waitingKey, userId)
			.map(removedCount -> removedCount > 0);
	}

	private String generateWaitingKey(String productId) {
		return "queue:waiting:" + productId;
	}
}
