package com.bidulgi.queueservice.infrastructure.event;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.bidulgi.queueservice.application.QueueService;
import com.bidulgi.queueservice.infrastructure.event.payload.ReservationCompletePayload;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {

	private static final String EVENT_TYPE_RESERVATION_COMPLETE = "reservationComplete";

	private final ObjectMapper objectMapper;
	private final QueueService queueService;

	@KafkaListener(topics = "reservation", groupId = "queue-service")
	public void onReservationEvent(
		@Payload String payload,
		@Header("eventType") String eventType
	) {
		if (!EVENT_TYPE_RESERVATION_COMPLETE.equals(eventType)) {
			return;
		}

		ReservationCompletePayload eventPayload = parsePayload(payload);

		log.info("예약 완료 이벤트 수신: userId={}, productId={}", eventPayload.userId(), eventPayload.productId());

		queueService.dequeue(eventPayload.userId(), eventPayload.productId())
			.doOnNext(result -> log.info("대기열 dequeue 완료. 다음 사용자 활성화: userId={}, productId={}, state={}",
				result.userId(), result.productId(), result.state()))
			.switchIfEmpty(Mono.fromRunnable(() ->
				log.info("대기열에 활성화할 다음 사용자가 없습니다. productId={}", eventPayload.productId())))
			.doOnError(error -> log.error("예약 완료 이벤트 처리 실패: {}", error.getMessage(), error))
			.onErrorComplete()
			.subscribe();
	}

	private ReservationCompletePayload parsePayload(String payload) {
		try {
			return objectMapper.readValue(payload, ReservationCompletePayload.class);
		} catch (Exception e) {
			throw new IllegalArgumentException("예약 완료 이벤트 파싱 실패", e);
		}
	}
}
