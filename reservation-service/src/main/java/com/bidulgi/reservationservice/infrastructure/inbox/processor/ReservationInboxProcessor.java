package com.bidulgi.reservationservice.infrastructure.inbox.processor;

import java.util.List;

import com.bidulgi.reservationservice.application.service.InboxService;
import com.bidulgi.reservationservice.domain.model.InboxStatus;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bidulgi.reservationservice.domain.model.Inbox;
import com.bidulgi.reservationservice.domain.repository.InboxRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationInboxProcessor {

	private final InboxRepository inboxRepository;
	private final InboxService inboxService;

	@Scheduled(fixedDelay = 5000) // 5초 주기
	public void process() {
		List<Inbox> inboxes = inboxRepository
			.findTop100ByStatusOrderByCreatedAtAsc(InboxStatus.PENDING);

		for (Inbox inbox : inboxes) {
			inboxService.processOne(inbox.getId());
		}
	}
}