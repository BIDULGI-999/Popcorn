package com.bidulgi.queueservice.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bidulgi.queueservice.application.dto.QueueResult;
import com.bidulgi.queueservice.application.port.TokenGenerator;
import com.bidulgi.queueservice.domain.vo.QueueState;
import com.bidulgi.queueservice.domain.repository.QueueRepository;
import com.bidulgi.queueservice.domain.repository.TokenRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class QueueService {

	private final QueueRepository queueRepository;
	private final TokenRepository tokenRepository;
	private final TokenGenerator tokenGenerator;

	public Mono<QueueResult> enqueue(String userId, String productId) {
		return queueRepository.enqueue(userId, productId)
			.flatMap(state -> {
				if (state == QueueState.WAIT) {
					return Mono.just(QueueResult.of(userId, productId, state));
				}

				return tokenRepository.findToken(userId, productId)
					.map(token -> QueueResult.of(userId, productId, state, token))
					.switchIfEmpty(tokenGenerator.createAccessToken(userId, productId)
						.flatMap(token -> tokenRepository.saveToken(userId, productId, token)
							.thenReturn(QueueResult.of(userId, productId, state, token)))
					);
			});
	}

	// TODO 예약완료 이벤트 수신 시 dequeue 로직 실행하도록 변경
	public Mono<QueueResult> dequeue(String userId, String productId) {
		return queueRepository.dequeue(userId, productId)
			.flatMap(result -> {
				List<String> results = parseDequeueResult(result);
				String nextProductId = results.get(0);
				String nextUserId = results.get(1);

				return tokenGenerator.createAccessToken(nextUserId, nextProductId)
					.flatMap(token -> tokenRepository.saveToken(nextUserId, nextProductId, token)
						.thenReturn(QueueResult.of(nextUserId, nextProductId, QueueState.ACTIVE, token)));
			});
	}

	private List<String> parseDequeueResult(String result) {
		String[] parts = result.split(":");
		if (parts.length != 2) {
			// TODO 커스텀 예외 처리
			throw new IllegalArgumentException("Invalid dequeue result format" + result);
		}
		return List.of(parts[0], parts[1]);
	}

}
