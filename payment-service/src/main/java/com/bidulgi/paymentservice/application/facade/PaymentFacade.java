package com.bidulgi.paymentservice.application.facade;

import org.springframework.stereotype.Component;

import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.paymentservice.application.dto.ConfirmPaymentResponse;
import com.bidulgi.paymentservice.application.service.PaymentService;
import com.bidulgi.paymentservice.domain.model.Payment;
import com.bidulgi.paymentservice.presentation.request.CreatePaymentRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

	private final PaymentService paymentService;

	public ConfirmPaymentResponse confirm(CreatePaymentRequest request, UserPrincipal user) {

		// TODO: 예약 검증 진행 해야함

		// ReservationResponse reservation = reservationClient.getReservationById(user.id());
		//
		// if (!reservation.amount().equals(request.amount())){
		// 	throw new Exception("결제 검증 실패");
		// }

		// 결제 전 상태 저장
		Payment payment = paymentService.readyPayment(request, user.id());

		// TODO: tossAPI 결제 승인 진행

		// 수정 필요
		return ConfirmPaymentResponse.from(payment);
	}
}
