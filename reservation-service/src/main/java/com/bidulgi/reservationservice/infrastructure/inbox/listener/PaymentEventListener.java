package com.bidulgi.reservationservice.infrastructure.inbox.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.bidulgi.reservationservice.application.service.InboxService;
import com.bidulgi.reservationservice.domain.model.inbox.Inbox;
import com.bidulgi.reservationservice.domain.repository.InboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

	private final InboxRepository inboxRepository;
	private final InboxService inboxService;

	@KafkaListener(topics = "payment", groupId = "reservation-service")
	public void onPaymentEvent(@Payload String payload, @Header("eventId") String eventId) {
	    if (inboxRepository.existsByEventId(eventId)) return;
	    Inbox inbox = Inbox.create(eventId, "PAYMENT_SUCCEEDED", payload);
	    inboxRepository.save(inbox);

		try {
			inboxService.processOne(inbox.getId());
		} catch (Exception e) {
			log.error("인박스 즉시 처리에 실패했습니다. id={}", inbox.getId(), e);
		}
	}
}