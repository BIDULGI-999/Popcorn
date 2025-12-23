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

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductStockEventListener {

	private static final String EVENT_TYPE_STOCK_DECREASED = "STOCK_DECREASED";
	private static final String EVENT_TYPE_STOCK_DECREASE_FAILED = "STOCK_DECREASE_FAILED";

	private final InboxRepository inboxRepository;
	private final InboxService inboxService;

	@KafkaListener(topics = "product", groupId = "reservation-service")
	public void onStockEvent(
		@Payload String payload,
		@Header("eventId") String eventId,
		@Header("eventType") String eventType
	) {
		if (!EVENT_TYPE_STOCK_DECREASED.equals(eventType) && !EVENT_TYPE_STOCK_DECREASE_FAILED.equals(eventType)) {
			return;
		}

		if (inboxRepository.existsByEventId(eventId)) {
			return;
		}

		Inbox inbox = Inbox.create(eventId, eventType, payload);
		inboxRepository.save(inbox);

		try {
			inboxService.processOne(inbox.getId());
		} catch (Exception e) {
			log.error("재고 이벤트 인박스 즉시 처리 실패. inboxId={}", inbox.getId(), e);
		}
	}
}
