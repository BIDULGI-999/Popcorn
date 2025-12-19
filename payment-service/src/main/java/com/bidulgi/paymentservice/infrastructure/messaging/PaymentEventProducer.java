package com.bidulgi.paymentservice.infrastructure.messaging;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.bidulgi.paymentservice.infrastructure.messaging.dto.PaymentEventPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bidulgi.paymentservice.application.port.out.PaymentEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer implements PaymentEventPublisher {

	private static final String TOPIC = "payment";

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public void publishPaymentSucceeded(String reservationId, UUID paymentId, int paidAmount) {
		PaymentEventPayload payload = new PaymentEventPayload(paymentId, paidAmount);

		try {
			String payloadJson = objectMapper.writeValueAsString(payload);

			kafkaTemplate.send(
				MessageBuilder.withPayload(payloadJson)
					.setHeader(KafkaHeaders.TOPIC, TOPIC)
					.setHeader("eventId", reservationId)
					.setHeader("eventType", "PAYMENT_SUCCEEDED")
					.build()
			);

			log.info("결제 완료 이벤트 발행 완료. reservationId={}, paymentId={}", reservationId, paymentId);
		} catch (JsonProcessingException e) {
			log.error("결제 이벤트 직렬화 실패. reservationId={}", reservationId, e);
			throw new RuntimeException("Failed to serialize payment event", e);
		}
	}

	@Override
	public void publishPaymentCanceled(UUID paymentId, int balanceAmount){
		PaymentEventPayload payload = new PaymentEventPayload(paymentId, balanceAmount);

		try{
			String payloadJson = objectMapper.writeValueAsString(payload);

			kafkaTemplate.send(
				MessageBuilder.withPayload(payloadJson)
					.setHeader(KafkaHeaders.TOPIC, TOPIC)
					.setHeader("eventId", paymentId)
					.setHeader("eventType", "PAYMENT_CANCELED")
					.build()
			);

			log.info("결제 취소 이벤트 발행 완료. paymentId={}", paymentId);
		} catch (JsonProcessingException e){
			log.error("결제 취소 이벤트 직렬화 실패. paymentId={}", paymentId, e);
			throw new RuntimeException("Failed to serialize payment cancel event", e);
		}
	}
}
