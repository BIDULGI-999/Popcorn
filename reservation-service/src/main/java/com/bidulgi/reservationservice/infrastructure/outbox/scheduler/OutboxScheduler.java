package com.bidulgi.reservationservice.infrastructure.outbox.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bidulgi.reservationservice.domain.model.outbox.Outbox;
import com.bidulgi.reservationservice.domain.model.outbox.OutboxStatus;
import com.bidulgi.reservationservice.domain.repository.OutboxRepository;
import com.bidulgi.reservationservice.infrastructure.outbox.producer.KafkaEventProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {
	private final OutboxRepository outboxRepository;
	private final KafkaEventProducer kafkaEventPublisher;

	@Scheduled(fixedDelay = 600000) // 10분 간격 (여유되면 1분, 10초, 5초 이런식으로 줄이기)
	@Transactional
	public void retryPendingMessages() {
		List<Outbox> eventList = outboxRepository.findByStatusIn(List.of(OutboxStatus.PENDING, OutboxStatus.FAILED));
		for (Outbox outbox : eventList) {
			try {
				kafkaEventPublisher.sendEvent(
					outbox.getAggregateType(),   // topic
					outbox.getEventId(),         // eventId (header & Outbox Unique key)
					outbox.getEventType(),       // eventType (header)
					outbox.getAggregateId(),     // record key
					outbox.getPayload()          // record message
				);
				outbox.markSuccess();
			} catch (Exception e) {
				outbox.markFailed();
			}
			outboxRepository.save(outbox);
		}
	}

}