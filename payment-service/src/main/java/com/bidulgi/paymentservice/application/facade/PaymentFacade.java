package com.bidulgi.paymentservice.application.facade;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.paymentservice.application.dto.ApprovePaymentCommand;
import com.bidulgi.paymentservice.application.dto.ConfirmPaymentResponse;
import com.bidulgi.paymentservice.application.service.PaymentService;
import com.bidulgi.paymentservice.domain.exception.PaymentErrorCode;
import com.bidulgi.paymentservice.domain.exception.PaymentException;
import com.bidulgi.paymentservice.domain.model.Payment;
import com.bidulgi.paymentservice.domain.model.PaymentStatus;
import com.bidulgi.paymentservice.infrastructure.client.ReservationClient;
import com.bidulgi.paymentservice.infrastructure.client.TossPaymentClient;
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

	public ConfirmPaymentResponse confirm(CreatePaymentRequest request, UserPrincipal user) {

		// 예약 확인 부분 연결 안됨
		// ReservationResponse reservation = reservationClient.getReservationById(UUID.fromString(request.orderId()));
		//
		// if (!reservation.amount().equals(request.amount())) {
		// 	throw new PaymentException(PaymentErrorCode.AMOUNT_MISMATCH);
		// }

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

		return ConfirmPaymentResponse.from(confirmedPayment);
	}

	// TODO: 결제 취소
}
