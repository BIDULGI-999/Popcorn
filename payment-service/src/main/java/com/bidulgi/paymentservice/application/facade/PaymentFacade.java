package com.bidulgi.paymentservice.application.facade;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.paymentservice.application.dto.ApplyCancelCommand;
import com.bidulgi.paymentservice.application.dto.ApprovePaymentCommand;
import com.bidulgi.paymentservice.application.dto.CancelPaymentCommand;
import com.bidulgi.paymentservice.application.dto.CancelPaymentResponse;
import com.bidulgi.paymentservice.application.dto.ConfirmPaymentCommand;
import com.bidulgi.paymentservice.application.dto.ConfirmPaymentResponse;
import com.bidulgi.paymentservice.application.port.out.PaymentGateway;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentCancelRequest;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentCancelResult;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentConfirmRequest;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentConfirmResult;
import com.bidulgi.paymentservice.application.service.PaymentService;
import com.bidulgi.paymentservice.domain.exception.PaymentErrorCode;
import com.bidulgi.paymentservice.domain.exception.PaymentException;
import com.bidulgi.paymentservice.domain.model.Payment;
import com.bidulgi.paymentservice.domain.model.PaymentStatus;
import com.bidulgi.paymentservice.application.port.out.PaymentEventPublisher;
import com.bidulgi.paymentservice.application.port.out.ReservationReader;
import com.bidulgi.paymentservice.application.port.out.dto.ReservationInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFacade {

	private final PaymentService paymentService;
	private final PaymentGateway paymentGateway;
	private final ReservationReader reservationReader;
	private final PaymentEventPublisher paymentEventPublisher;

	public ConfirmPaymentResponse confirm(ConfirmPaymentCommand command, UserPrincipal user) {

		// 예약 정보 조회 및 금액 검증
		ReservationInfo reservation = reservationReader.getReservation(UUID.fromString(command.orderId()));

		if (!reservation.amount().equals(command.amount())) {
			throw new PaymentException(PaymentErrorCode.AMOUNT_MISMATCH);
		}

		// 기존 결제 확인
		Payment payment = paymentService.findByOrderId(command.orderId());

		if (payment != null) {
			// 이미 완료된 결제
			if (payment.getStatus() == PaymentStatus.DONE) {
				throw new PaymentException(PaymentErrorCode.ALREADY_APPROVED);
			}
			// READY 상태면 기존 Payment 재사용 (재시도 케이스)
		} else {
			// 신규 결제 생성
			payment = paymentService.readyPayment(command, user.id());
		}

		// 결제 승인
		PaymentConfirmRequest confirmRequest = new PaymentConfirmRequest(
			command.paymentKey(),
			command.orderId(),
			command.amount()
		);
		PaymentConfirmResult confirmResult = paymentGateway.confirm(confirmRequest);
		ApprovePaymentCommand approveCommand = ApprovePaymentCommand.from(confirmResult);

		Payment confirmedPayment = paymentService.confirmPayment(payment.getId(), approveCommand);

		// 결제 완료 이벤트 발행 (reservation-service로 전달)
		paymentEventPublisher.publishPaymentSucceeded(
			command.orderId(),
			confirmedPayment.getId(),
			confirmedPayment.getPrice()
		);

		return ConfirmPaymentResponse.from(confirmedPayment);
	}

	public CancelPaymentResponse cancel(CancelPaymentCommand request, UserPrincipal user) {

		Payment payment = paymentService.findByPaymentKey(request.paymentKey());

		payment.validateCancelAmount(request.cancelAmount());
		if (!payment.canCancel()) {
			if (payment.isCanceled()) {
				throw new PaymentException(PaymentErrorCode.ALREADY_CANCELED);
			}
			throw new PaymentException(PaymentErrorCode.INVALID_CANCEL);
		}

		PaymentCancelRequest cancelRequest = new PaymentCancelRequest(
			request.cancelReason(),
			request.cancelAmount()
		);

		PaymentCancelResult cancelResult = paymentGateway.cancel(cancelRequest, request.paymentKey());
		ApplyCancelCommand command = ApplyCancelCommand.from(cancelResult);
		Payment cancelledPayment = paymentService.cancelPayment(payment.getId(), command);

		// 결제 취소 이벤트 발행
		paymentEventPublisher.publishPaymentCanceled(
			cancelledPayment.getId(),
			cancelledPayment.getBalanceAmount()
		);

		return CancelPaymentResponse.from(cancelledPayment);
	}
}
