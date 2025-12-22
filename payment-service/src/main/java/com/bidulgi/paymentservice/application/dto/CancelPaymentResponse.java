package com.bidulgi.paymentservice.application.dto;

import java.util.UUID;

import com.bidulgi.paymentservice.domain.model.Payment;

public record CancelPaymentResponse (
	UUID paymentId,
	String orderId,
	String paymentKey,
	Integer canceledAmount,
	Integer balanceAmount,
	String status
) {
	public static CancelPaymentResponse from(Payment payment) {
		return new CancelPaymentResponse(
			payment.getId(),
			payment.getOrderId(),
			payment.getPaymentKey(),
			payment.getPrice() - payment.getBalanceAmount(),
			payment.getBalanceAmount(),
			payment.getStatus().name()
		);
	}
}
