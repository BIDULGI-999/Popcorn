package com.bidulgi.paymentservice.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidulgi.paymentservice.application.dto.ApplyCancelCommand;
import com.bidulgi.paymentservice.application.dto.ApprovePaymentCommand;
import com.bidulgi.paymentservice.application.dto.CancelPaymentCommand;
import com.bidulgi.paymentservice.domain.exception.PaymentErrorCode;
import com.bidulgi.paymentservice.domain.exception.PaymentException;
import com.bidulgi.paymentservice.domain.model.Payment;
import com.bidulgi.paymentservice.domain.model.PaymentHistory;
import com.bidulgi.paymentservice.domain.repository.PaymentHistoryRepository;
import com.bidulgi.paymentservice.domain.repository.PaymentRepository;
import com.bidulgi.paymentservice.presentation.request.CreatePaymentRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;

	@Transactional
	public Payment readyPayment(CreatePaymentRequest request, UUID userId) {

		Payment newPayment;
		PaymentHistory paymentHistory;

		newPayment = Payment.builder()
			.paymentKey(request.paymentKey())
			.orderId(request.orderId())
			.price(request.amount())
			.userId(userId)
			.build();

		paymentHistory = PaymentHistory.builder()
			.payment(newPayment)
			.amount(request.amount())
			.build();

		paymentRepository.save(newPayment);
		paymentHistoryRepository.save(paymentHistory);

		return newPayment;
	}

	@Transactional
	public Payment confirmPayment(UUID paymentId, ApprovePaymentCommand command) {

		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND, paymentId.toString()));

		payment.approve(
			command.status(),
			command.method(),
			command.approvedAt(),
			command.isPartialCancelable()
		);

		PaymentHistory history = PaymentHistory.builder()
			.payment(payment)
			.amount(payment.getPrice())
			.build();

		paymentRepository.save(payment);
		paymentHistoryRepository.save(history);

		return payment;
	}

	@Transactional
	public Payment cancelPayment(UUID paymentId, ApplyCancelCommand command) {

		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND, paymentId.toString()));

		payment.cancel(
			command.status(),
			command.canceledAt(),
			command.cancelAmount()
		);

		PaymentHistory history = PaymentHistory.builder()
			.payment(payment)
			.amount(payment.getPrice())
			.build();

		paymentRepository.save(payment);
		paymentHistoryRepository.save(history);

		return payment;
	}

	@Transactional(readOnly = true)
	public Payment findByOrderId(String orderId) {
		return paymentRepository.findByOrderId(orderId).orElse(null);
	}

	@Transactional(readOnly = true)
	public Payment findByPaymentKey(String paymentKey) {
		return paymentRepository.findByPaymentKey(paymentKey).orElse(null);
	}
}
