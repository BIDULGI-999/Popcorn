package com.bidulgi.reservationservice.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidulgi.common.globalException.custom.EntityNotFoundException;
import com.bidulgi.common.globalException.custom.InternalServiceException;
import com.bidulgi.reservationservice.domain.model.Inbox;
import com.bidulgi.reservationservice.domain.model.InboxStatus;
import com.bidulgi.reservationservice.domain.model.Reservation;
import com.bidulgi.reservationservice.domain.repository.InboxRepository;
import com.bidulgi.reservationservice.domain.repository.ReservationRepository;
import com.bidulgi.reservationservice.infrastructure.inbox.payload.PaymentEventPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InboxService {

	private final InboxRepository inboxRepository;
	private final ReservationRepository reservationRepository;
	private final ObjectMapper objectMapper;

	@Transactional
	public void processOne(Long inboxId) {
		Inbox inbox = inboxRepository.findById(inboxId)
			.orElseThrow(() -> new EntityNotFoundException("Inbox not found. id=" + inboxId));

		if (inbox.getStatus() != InboxStatus.PENDING) return;

		try {
			handle(inbox);
			inbox.markProcessed();
		} catch (Exception e) {
			inbox.incrementTry();
		}
	}

	private void handle(Inbox inbox) {
		UUID reservationId = UUID.fromString(inbox.getEventId());

		Reservation reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));

		if (!reservation.isPending()) return;

		PaymentEventPayload payload = parsePayload(inbox.getPayload());

		int expectedAmount = reservation.getAmount();
		int paidAmount = payload.paidAmount();

		if (expectedAmount != paidAmount) {
			throw new InternalServiceException("결제 금액이 일치하지 않습니다. expected=" + expectedAmount + ", paid=" + paidAmount);
		}
		reservation.complete(payload.paymentId());
	}

	private PaymentEventPayload parsePayload(String payloadJson) {
		try {
			return objectMapper.readValue(payloadJson, PaymentEventPayload.class);
		} catch (JsonProcessingException e) {
			throw new InternalServiceException("Failed to parse payment event payload");
		}
	}
}
