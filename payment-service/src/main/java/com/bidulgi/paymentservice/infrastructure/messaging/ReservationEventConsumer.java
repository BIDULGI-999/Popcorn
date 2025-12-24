package com.bidulgi.paymentservice.infrastructure.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.bidulgi.paymentservice.application.service.PaymentCompensationService;
import com.bidulgi.paymentservice.infrastructure.messaging.dto.ReservationFailedPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventConsumer {

	private static final int MAX_RETRY = 3;

	private final PaymentCompensationService compensationService;
	private final ObjectMapper objectMapper;

	@KafkaListener(topics = "reservation", groupId = "payment-service")
	public void handleReservationEvent(
		ConsumerRecord<String, String> record,
		@Header(name = "eventType", required = false) String eventTypeHeader,
		@Header(name = "kafka_deliveryAttempt", required = false) Integer deliveryAttempt)
	{
		int attempt = (deliveryAttempt != null) ? deliveryAttempt : 1;

		// 헤더에서 eventType을 가져오거나, 없으면 메시지 본문에서 파싱
		String eventType = eventTypeHeader;
		if (eventType == null) {
			eventType = extractEventTypeFromPayload(record.value());
		}

		log.info("예약 이벤트 수신. eventType={}, attempt={}", eventType, attempt);

		try {
			if ("RESERVATION_FAILED".equals(eventType)) {
				handleReservationFailed(record.value());
			}

		} catch (Exception e) {
			log.error("예약 이벤트 처리 실패. eventType={}", eventType, e);

			if (attempt >= MAX_RETRY) {
				// 최대 재시도 초과 → 실패 기록만 하고 예외 안 던짐
				log.error("최대 재시도 초과. 수동 처리 필요. payload={}", record.value());
				// TODO: 운영팀 알림
				return;
			}

			throw e;
		}
	}

	private String extractEventTypeFromPayload(String payload) {
		try {
			JsonNode node = objectMapper.readTree(payload);
			JsonNode eventTypeNode = node.get("eventType");
			return eventTypeNode != null ? eventTypeNode.asText() : null;
		} catch (JsonProcessingException e) {
			log.warn("eventType 파싱 실패. payload={}", payload);
			return null;
		}
	}

	private void handleReservationFailed(String payload) {
		try {
			ReservationFailedPayload failedPayload = objectMapper.readValue(
				payload,
				ReservationFailedPayload.class
			);

			log.warn("예약 실패 이벤트 수신. reservationId={}, paymentId={}, reason={}",
				failedPayload.reservationId(),
				failedPayload.paymentId(),
				failedPayload.reason()
			);

			// 보상 트랜잭션 실행
			compensationService.compensatePayment(failedPayload.paymentId());

		} catch (JsonProcessingException e) {
			log.error("예약 실패 이벤트 파싱 실패. payload={}", payload, e);
			throw new RuntimeException("Failed to parse reservation failed event", e);
		}
	}
}
