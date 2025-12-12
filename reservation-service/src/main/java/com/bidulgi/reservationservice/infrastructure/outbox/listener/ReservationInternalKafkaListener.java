package com.bidulgi.reservationservice.infrastructure.outbox.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.bidulgi.reservationservice.domain.model.outbox.OutboxStatus;
import com.bidulgi.reservationservice.domain.repository.OutboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationInternalKafkaListener {
	private final OutboxRepository outboxRepository;

	@KafkaListener(topics = "reservation", groupId = "reservation-international-group")
	public void consumeChatEvents(String message, @Header("eventId") String eventId) {
		outboxRepository.findByEventId(eventId).ifPresent(outbox -> {
			if (outbox.getStatus() != OutboxStatus.SUCCESS) {
				outbox.markSuccess();
				outboxRepository.save(outbox);
			}
		});
	}
}
