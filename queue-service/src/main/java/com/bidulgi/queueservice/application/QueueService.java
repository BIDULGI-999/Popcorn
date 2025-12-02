package com.bidulgi.queueservice.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bidulgi.queueservice.application.dto.QueueResult;
import com.bidulgi.queueservice.application.port.TokenGenerator;
import com.bidulgi.queueservice.domain.model.QueueState;
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
					return Mono.just(QueueResult.of(productId, state));
				}

				return tokenRepository.findToken(userId, productId)
					.map(token -> QueueResult.of(productId, state, token))
					.switchIfEmpty(tokenGenerator.createAccessToken(userId, productId)
						.flatMap(token -> tokenRepository.saveToken(userId, productId, token)
							.thenReturn(QueueResult.of(productId, state, token)))
					);
			});
	}

}
