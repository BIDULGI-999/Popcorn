package com.bidulgi.reservationservice.application.service;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidulgi.common.globalException.custom.EntityNotFoundException;
import com.bidulgi.common.globalException.custom.InternalServiceException;
import com.bidulgi.reservationservice.domain.model.inbox.Inbox;
import com.bidulgi.reservationservice.domain.model.inbox.InboxStatus;
import com.bidulgi.reservationservice.domain.model.Reservation;
import com.bidulgi.reservationservice.domain.model.ReservationStatus;
import com.bidulgi.reservationservice.domain.repository.InboxRepository;
import com.bidulgi.reservationservice.domain.repository.ReservationRepository;
import com.bidulgi.reservationservice.infrastructure.inbox.payload.StockDecreaseResultPayload;
import com.bidulgi.reservationservice.infrastructure.inbox.payload.PaymentEventPayload;
import com.bidulgi.reservationservice.infrastructure.outbox.event.ReservationCompleteEvent;
import com.bidulgi.reservationservice.infrastructure.outbox.payload.ReservationCompletePayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InboxService {

	private final InboxRepository inboxRepository;
	private final ReservationRepository reservationRepository;
	private final ObjectMapper objectMapper;
	private final ApplicationEventPublisher eventPublisher;

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
		String type = inbox.getEventType();

		// TODO 이벤트 타입 표현 방법 통일 필요
		switch (type) {
			case "paymentSucceeded" -> handlePaymentSucceeded(inbox);
			case "paymentCanceled" -> handlePaymentCanceled(inbox);
			case "STOCK_DECREASED" -> handleStockDecreased(inbox);
			case "STOCK_DECREASE_FAILED" -> handleStockDecreaseFailed(inbox);
			default -> throw new InternalServiceException("지원하지 않는 이벤트 타입입니다. type=" + type);
		}
	}

	private void handlePaymentSucceeded(Inbox inbox) {
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

		eventPublisher.publishEvent(
			new ReservationCompleteEvent(reservation.getId().toString(),
				ReservationCompletePayload.from(reservation))
		);
	}

	private void handlePaymentCanceled(Inbox inbox) {
		UUID reservationId = UUID.fromString(inbox.getEventId());

		Reservation reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));

		if (reservation.isCanceled()) return;

		if (!reservation.canCancel()) {
			throw new InternalServiceException("취소할 수 없는 예약 상태입니다. status=" + reservation.getStatus());
		}

		reservation.cancel();
	}

	private void handleStockDecreased(Inbox inbox) {
		StockDecreaseResultPayload payload = parseStockPayload(inbox.getPayload());
		Reservation reservation = reservationRepository.findById(payload.reservationId())
			.orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + payload.reservationId()));

		if (reservation.getStatus() != ReservationStatus.REQUESTED) {
			return; // 이미 다른 상태로 진행된 경우 무시
		}

		reservation.markHold();
	}

	private void handleStockDecreaseFailed(Inbox inbox) {
		StockDecreaseResultPayload payload = parseStockPayload(inbox.getPayload());
		Reservation reservation = reservationRepository.findById(payload.reservationId())
			.orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + payload.reservationId()));

		if (reservation.getStatus() == ReservationStatus.COMPLETED ||
			reservation.getStatus() == ReservationStatus.PENDING
		) {
			return; // 결제 진행/완료된 경우 재고 실패로 덮어쓰지 않음
		}

		reservation.markFailed();
	}

	private PaymentEventPayload parsePayload(String payloadJson) {
		try {
			return objectMapper.readValue(payloadJson, PaymentEventPayload.class);
		} catch (JsonProcessingException e) {
			throw new InternalServiceException("Failed to parse payment event payload");
		}
	}

	private StockDecreaseResultPayload parseStockPayload(String payloadJson) {
		try {
			return objectMapper.readValue(payloadJson, StockDecreaseResultPayload.class);
		} catch (JsonProcessingException e) {
			throw new InternalServiceException("Failed to parse stock decrease payload");
		}
	}
}
