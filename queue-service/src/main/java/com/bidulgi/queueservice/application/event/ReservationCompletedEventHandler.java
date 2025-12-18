package com.bidulgi.queueservice.application.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bidulgi.queueservice.application.QueueService;
import com.bidulgi.queueservice.domain.event.ReservationCompletedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCompletedEventHandler {

	private final QueueService queueService;

	@EventListener
	public void handleReservationCompletedEvent(ReservationCompletedEvent event) {
		queueService.dequeue(event.userId(), event.productId())
			.doOnNext(result -> log.info("대기열 dequeue 완료. 다음 사용자 활성화: userId={}, productId={}, state={}",
				result.userId(), result.productId(), result.state()))
			.switchIfEmpty(Mono.fromRunnable(() ->
				log.info("대기열에 활성화할 다음 사용자가 없습니다. productId={}", event.productId())))
			.doOnError(error -> log.error("예약 완료 이벤트 처리 실패: {}", error.getMessage(), error))
			.onErrorComplete()
			.subscribe();
	}
}
