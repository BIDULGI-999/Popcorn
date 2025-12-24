package com.bidulgi.queueservice.infrastructure.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.bidulgi.queueservice.domain.event.ReservationCompletedEvent;
import com.bidulgi.queueservice.infrastructure.event.payload.ReservationCompletePayload;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {

	private static final String EVENT_TYPE_RESERVATION_COMPLETE = "reservationComplete";

	private final ObjectMapper objectMapper;
	private final ApplicationEventPublisher eventPublisher;

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

		eventPublisher.publishEvent(ReservationCompletedEvent.of(eventPayload.userId(), eventPayload.productId()));
	}

	private ReservationCompletePayload parsePayload(String payload) {
		try {
			return objectMapper.readValue(payload, ReservationCompletePayload.class);
		} catch (Exception e) {
			throw new IllegalArgumentException("예약 완료 이벤트 파싱 실패", e);
		}
	}
}
