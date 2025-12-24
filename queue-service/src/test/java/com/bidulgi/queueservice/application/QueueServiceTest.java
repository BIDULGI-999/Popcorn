package com.bidulgi.queueservice.application;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bidulgi.queueservice.application.dto.QueueResult;
import com.bidulgi.queueservice.application.port.TokenGenerator;
import com.bidulgi.queueservice.domain.repository.QueueRepository;
import com.bidulgi.queueservice.domain.repository.TokenRepository;
import com.bidulgi.queueservice.domain.vo.DequeueResult;
import com.bidulgi.queueservice.domain.vo.QueueState;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

	@Mock
	private QueueRepository queueRepository;

	@Mock
	private TokenRepository tokenRepository;

	@Mock
	private TokenGenerator tokenGenerator;

	@InjectMocks
	private QueueService queueService;

	@Test
	void enqueue_WhenWaiting_ShouldReturnWaitStateWithoutToken() {
		UUID userId = UUID.randomUUID();
		UUID productId = UUID.randomUUID();

		given(queueRepository.enqueue(userId, productId))
			.willReturn(Mono.just(QueueState.WAIT));

		Mono<QueueResult> resultMono = queueService.enqueue(userId, productId);

		StepVerifier.create(resultMono)
			.expectNextMatches(result ->
				result.userId().equals(userId)
					&& result.productId().equals(productId)
					&& result.state().equals(QueueState.WAIT.name())
					&& result.token() == null
			)
			.verifyComplete();

		verify(queueRepository).enqueue(userId, productId);
		verifyNoInteractions(tokenGenerator, tokenRepository);
	}

	@Test
	void enqueue_WhenActiveAndNoToken_ShouldGenerateAndSaveToken() {
		UUID userId = UUID.randomUUID();
		UUID productId = UUID.randomUUID();
		String newToken = "new-token";

		given(queueRepository.enqueue(userId, productId))
			.willReturn(Mono.just(QueueState.ACTIVE));
		given(tokenRepository.findToken(userId, productId))
			.willReturn(Mono.empty());
		given(tokenGenerator.createAccessToken(userId, productId))
			.willReturn(Mono.just(newToken));
		given(tokenRepository.saveToken(userId, productId, newToken))
			.willReturn(Mono.just(Boolean.TRUE));

		Mono<QueueResult> resultMono = queueService.enqueue(userId, productId);

		StepVerifier.create(resultMono)
			.expectNextMatches(result ->
				result.userId().equals(userId)
					&& result.productId().equals(productId)
					&& result.state().equals(QueueState.ACTIVE.name())
					&& newToken.equals(result.token())
			)
			.verifyComplete();

		verify(queueRepository).enqueue(userId, productId);
		verify(tokenRepository).findToken(userId, productId);
		verify(tokenGenerator).createAccessToken(userId, productId);
		verify(tokenRepository).saveToken(userId, productId, newToken);
	}

	@Test
	void dequeue_ShouldActivateNextUserAndReturnQueueResult() {
		UUID currentUserId = UUID.randomUUID();
		UUID currentProductId = UUID.randomUUID();

		UUID nextUserId = UUID.randomUUID();
		UUID nextProductId = UUID.randomUUID();

		String issuedToken = "issued-token";

		given(queueRepository.dequeue(currentUserId, currentProductId))
			.willReturn(Mono.just(new DequeueResult(nextUserId, nextProductId)));
		given(tokenGenerator.createAccessToken(nextUserId, nextProductId))
			.willReturn(Mono.just(issuedToken));
		given(tokenRepository.saveToken(nextUserId, nextProductId, issuedToken))
			.willReturn(Mono.just(Boolean.TRUE));

		Mono<QueueResult> resultMono = queueService.dequeue(currentUserId, currentProductId);

		StepVerifier.create(resultMono)
			.expectNextMatches(result ->
				result.userId().equals(nextUserId)
					&& result.productId().equals(nextProductId)
					&& result.state().equals(QueueState.ACTIVE.name())
					&& issuedToken.equals(result.token())
			)
			.verifyComplete();

		verify(queueRepository).dequeue(currentUserId, currentProductId);
		verify(tokenGenerator).createAccessToken(nextUserId, nextProductId);
		verify(tokenRepository).saveToken(nextUserId, nextProductId, issuedToken);
	}
}
