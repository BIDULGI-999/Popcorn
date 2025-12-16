package com.bidulgi.queueservice.application;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bidulgi.queueservice.application.dto.PositionResult;
import com.bidulgi.queueservice.application.dto.QueueResult;
import com.bidulgi.queueservice.application.port.TokenGenerator;
import com.bidulgi.queueservice.domain.vo.QueueState;
import com.bidulgi.queueservice.domain.repository.QueueRepository;
import com.bidulgi.queueservice.domain.repository.TokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final QueueRepository queueRepository;
	private final TokenRepository tokenRepository;
	private final TokenGenerator tokenGenerator;

	/**
	 * 대기열 등록
	 * @param userId 유저 아이디
	 * @param productId 상품 아이디
	 * @return 대기열 등록 결과
	 */
	public Mono<QueueResult> enqueue(UUID userId, UUID productId) {
		return queueRepository.enqueue(userId, productId)
			.flatMap(state -> {
				if (state == QueueState.WAIT) { // 대기 시 토큰 발급 없음
					return Mono.just(QueueResult.of(userId, productId, state));
				}

				// 활성화 시 토큰 발급 및 저장
				// 1. 기존 토큰이 있다면 결과 반환
				// 2. 기존 토큰이 없다면 새 토큰 발급 및 저장
				return tokenRepository.findToken(userId, productId)
					.map(token -> QueueResult.of(userId, productId, state, token))
					.switchIfEmpty(tokenGenerator.createAccessToken(userId, productId)
						.flatMap(token -> tokenRepository.saveToken(userId, productId, token)
							.thenReturn(QueueResult.of(userId, productId, state, token)))
					);
			});
	}

	/**
	 * 대기열 제거 및 다음 사용자 활성화
	 * @param userId 유저 아이디
	 * @param productId 상품 아이디
	 * @return 다음 사용자 대기열 결과
	 */
	public Mono<QueueResult> dequeue(UUID userId, UUID productId) {
		return queueRepository.dequeue(userId, productId)
			.flatMap(result -> {
				List<String> results = parseDequeueResult(result);
				UUID nextProductId = UUID.fromString(results.get(0));
				UUID nextUserId = UUID.fromString(results.get(1));

				return tokenGenerator.createAccessToken(nextUserId, nextProductId) // 토큰 발급 후 저장 및 결과 반환
					.flatMap(token -> tokenRepository.saveToken(nextUserId, nextProductId, token)
						.thenReturn(QueueResult.of(nextUserId, nextProductId, QueueState.ACTIVE, token)));
			});
	}

	// "productId:userId" 형식의 결과를 파싱하여 리스트로 반환
	private List<String> parseDequeueResult(String result) {
		String[] parts = result.split(":");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Invalid dequeue result format" + result);
		}
		return List.of(parts[0], parts[1]);
	}

	/**
	 * 대기열 순번 구독
	 * @param userId 유저 아이디
	 * @param productId 상품 아이디
	 * @return 대기열 순번 스트림
	 */
	public Flux<PositionResult> subscribePosition(UUID userId, UUID productId) {
		return queueRepository.getPosition(userId, productId)
			.expand(position -> {
				Duration interval = calculateInterval(position);
				return Mono.delay(interval)
					.then(queueRepository.getPosition(userId, productId)); // 일정 시간 후 다시 순번 조회
			})
			.takeWhile(position -> position > 0)
			.map(PositionResult::of)
			.concatWith(
				tokenRepository.findToken(userId, productId)
					.map(PositionResult::of) // 토큰이 발급되면 마지막으로 전송
			)
			.doOnCancel(() -> removeFromQueue(userId, productId)); // SSE 연결 종료 시 대기열에서 제거
	}

	// 대기열 순번에 따른 조회 간격 계산
	private Duration calculateInterval(Long position) {
		if (position <= 1000) {
			return Duration.ofSeconds(1);
		} else if (position <= 5000) {
			return Duration.ofSeconds(3);
		} else {
			return Duration.ofSeconds(10);
		}
	}

	private void removeFromQueue(UUID userId, UUID productId) {
		queueRepository.remove(userId, productId)
			.doOnError(error -> {
				log.error("Failed to remove from queue. userId={}, productId={}", userId, productId, error);
			})
			.subscribe();
	}

}
