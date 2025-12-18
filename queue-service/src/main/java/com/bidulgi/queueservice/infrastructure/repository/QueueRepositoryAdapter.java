package com.bidulgi.queueservice.infrastructure.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

import com.bidulgi.queueservice.domain.vo.DequeueResult;
import com.bidulgi.queueservice.domain.vo.QueueState;
import com.bidulgi.queueservice.domain.repository.QueueRepository;
import com.bidulgi.queueservice.infrastructure.redis.LuaScriptProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryAdapter implements QueueRepository {

	private final ReactiveRedisTemplate<String, String> redisTemplate;
	private final LuaScriptProvider luaScriptProvider;
	private final ObjectMapper objectMapper;

	private static final String ACTIVE_KEY = "queue:active";
	private static final String WAITING_PRODUCTS_KEY = "queue:waiting:products";

	@Value("${queue.active-limit}")
	private int activeLimit;

	@Override
	public Mono<QueueState> enqueue(UUID userId, UUID productId) {
		String waitingKey = generateWaitingKey(productId);
		return redisTemplate.execute(
			luaScriptProvider.getEnqueueScript(),
			List.of(ACTIVE_KEY, waitingKey, WAITING_PRODUCTS_KEY),
			String.valueOf(userId),
			String.valueOf(Instant.now().toEpochMilli()),
			String.valueOf(activeLimit),
			String.valueOf(productId)
		).single().map(QueueState::of);
	}

	@Override
	public Mono<DequeueResult> dequeue(UUID userId, UUID productId) {
		String valueToRemove = productId + ":" + userId;
		return redisTemplate.execute(
			luaScriptProvider.getDequeueScript(),
			List.of(ACTIVE_KEY, WAITING_PRODUCTS_KEY),
			valueToRemove
		).singleOrEmpty()
			.flatMap(this::parseDequeueResult);
	}

	@Override
	public Mono<Long> getPosition(UUID userId, UUID productId) {
		String waitingKey = generateWaitingKey(productId);
		return redisTemplate.opsForZSet()
			.rank(waitingKey, String.valueOf(userId))
			.map(index -> index + 1);
	}

	@Override
	public Mono<Boolean> remove(UUID userId, UUID productId) {
		String waitingKey = generateWaitingKey(productId);
		return redisTemplate.opsForZSet()
			.remove(waitingKey, String.valueOf(userId))
			.map(removedCount -> removedCount > 0);
	}

	private String generateWaitingKey(UUID productId) {
		return "queue:waiting:" + productId;
	}

	private Mono<DequeueResult> parseDequeueResult(String jsonResult) {
		try {
			DequeueResultPayload payload = objectMapper.readValue(jsonResult, DequeueResultPayload.class);
			return Mono.just(new DequeueResult(
				UUID.fromString(payload.userId()),
				UUID.fromString(payload.productId())
			));
		} catch (Exception e) {
			return Mono.error(new IllegalArgumentException("Invalid dequeue result format: " + jsonResult, e));
		}
	}

	private record DequeueResultPayload(String userId, String productId) {}
}
