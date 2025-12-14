package com.bidulgi.paymentservice.application.facade;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.paymentservice.application.dto.ApplyCancelCommand;
import com.bidulgi.paymentservice.application.dto.ApprovePaymentCommand;
import com.bidulgi.paymentservice.application.dto.CancelPaymentCommand;
import com.bidulgi.paymentservice.application.dto.CancelPaymentResponse;
import com.bidulgi.paymentservice.application.dto.ConfirmPaymentResponse;
import com.bidulgi.paymentservice.application.service.PaymentService;
import com.bidulgi.paymentservice.domain.exception.PaymentErrorCode;
import com.bidulgi.paymentservice.domain.exception.PaymentException;
import com.bidulgi.paymentservice.domain.model.Payment;
import com.bidulgi.paymentservice.domain.model.PaymentStatus;
import com.bidulgi.paymentservice.infrastructure.client.ReservationClient;
import com.bidulgi.paymentservice.infrastructure.client.TossPaymentClient;
import com.bidulgi.paymentservice.infrastructure.client.dto.CancelTossRequest;
import com.bidulgi.paymentservice.infrastructure.client.dto.CancelTossResponse;
import com.bidulgi.paymentservice.infrastructure.messaging.PaymentEventProducer;
import com.bidulgi.paymentservice.infrastructure.client.dto.ConfirmTossRequest;
import com.bidulgi.paymentservice.infrastructure.client.dto.ConfirmTossResponse;
import com.bidulgi.paymentservice.infrastructure.client.dto.ReservationResponse;
import com.bidulgi.paymentservice.presentation.request.CreatePaymentRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFacade {

	private final PaymentService paymentService;
	private final TossPaymentClient tossPaymentClient;
	private final ReservationClient reservationClient;
	private final PaymentEventProducer paymentEventProducer;

	public ConfirmPaymentResponse confirm(CreatePaymentRequest request, UserPrincipal user) {

		// 예약 정보 조회 및 금액 검증
		ReservationResponse reservation = reservationClient.getReservationById(UUID.fromString(request.orderId()))
			.data();

		if (!reservation.amount().equals(request.amount())) {
			throw new PaymentException(PaymentErrorCode.AMOUNT_MISMATCH);
		}

		// 기존 결제 확인
		Payment payment = paymentService.findByOrderId(request.orderId());

		if (payment != null) {
			// 이미 완료된 결제
			if (payment.getStatus() == PaymentStatus.DONE) {
				throw new PaymentException(PaymentErrorCode.ALREADY_APPROVED);
			}
			// READY 상태면 기존 Payment 재사용 (재시도 케이스)
		} else {
			// 신규 결제 생성
			payment = paymentService.readyPayment(request, user.id());
		}

		// 토스 API 결제 승인
		ConfirmTossRequest tossRequest = new ConfirmTossRequest(
			request.paymentKey(),
			request.orderId(),
			request.amount()
		);
		ConfirmTossResponse tossResponse = tossPaymentClient.confirm(tossRequest);
		ApprovePaymentCommand command = ApprovePaymentCommand.from(tossResponse);

		Payment confirmedPayment = paymentService.confirmPayment(payment.getId(), command);

		// 결제 완료 이벤트 발행 (reservation-service로 전달)
		paymentEventProducer.publishPaymentSucceeded(
			request.orderId(),
			confirmedPayment.getId(),
			confirmedPayment.getPrice()
		);

		return ConfirmPaymentResponse.from(confirmedPayment);
	}

	// TODO: 결제 취소
	public CancelPaymentResponse cancel(CancelPaymentCommand request, UserPrincipal user) {

		Payment payment = paymentService.findByPaymentKey(request.paymentKey());

		if (payment == null) {
			throw new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND);
		}

		if(!payment.isPartialCancelable() && !Objects.equals(payment.getPrice(), request.cancelAmount())){
			throw new PaymentException(PaymentErrorCode.INVALID_CANCEL);
		}

		if(request.cancelAmount() > payment.getBalanceAmount()) {
			throw new PaymentException(PaymentErrorCode.CANCEL_AMOUNT_EXCEEDED);
		}

		if(payment.getStatus() != PaymentStatus.DONE && payment.getStatus() != PaymentStatus.PARTIAL_CANCELED) {
			if(payment.getStatus() == PaymentStatus.CANCELED) {
				throw new PaymentException(PaymentErrorCode.ALREADY_CANCELED);
			}
			throw new PaymentException(PaymentErrorCode.INVALID_CANCEL);
		}

		CancelTossRequest cancelTossRequest = new CancelTossRequest(
			request.cancelReason(),
			request.cancelAmount()
		);

		CancelTossResponse cancelTossResponse = tossPaymentClient.cancel(cancelTossRequest, request.paymentKey());
		ApplyCancelCommand command = ApplyCancelCommand.from(cancelTossResponse);
		Payment cancelledPayment = paymentService.cancelPayment(payment.getId(), command);

		// 결제 취소 이벤트 발행
		paymentEventProducer.publishPaymentCanceled(
			cancelledPayment.getId(),
			cancelledPayment.getBalanceAmount()
		);

		return CancelPaymentResponse.from(cancelledPayment);
	}
}
