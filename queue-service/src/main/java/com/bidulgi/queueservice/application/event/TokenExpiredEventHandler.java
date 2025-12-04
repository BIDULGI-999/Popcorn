package com.bidulgi.queueservice.application.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bidulgi.queueservice.application.QueueService;
import com.bidulgi.queueservice.domain.event.TokenExpiredEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenExpiredEventHandler {

	private final QueueService queueService;

	@EventListener
	public void handleTokenExpiredEvent(TokenExpiredEvent event) {
		String userId = event.userId();
		String productId = event.productId();

		log.info("토큰 만료 이벤트 처리: userId={}, productId={}", userId, productId);

		queueService.dequeue(userId, productId)
			.doOnNext(result -> log.info("다음 사용자 활성화: userId={}, productId={}, state={}",
				result.userId(), result.productId(), result.state()))
			.switchIfEmpty(Mono.fromRunnable(() ->
				log.info("대기열에 다음 사용자 없음: productId={}", productId)))
			.doOnError(error -> log.error("대기열 처리 실패: error={}", error.getMessage()))
			.onErrorComplete()
			.subscribe();
	}
}
