package com.bidulgi.paymentservice.application.dto;

import java.util.UUID;

import com.bidulgi.paymentservice.domain.model.Payment;

public record ConfirmPaymentResponse(
	UUID paymentId,
	String orderId,
	String paymentKey,
	Integer amount,
	String status
) {
	public static ConfirmPaymentResponse from(Payment payment) {
		return new ConfirmPaymentResponse(
			payment.getId(),
			payment.getOrderId(),
			payment.getPaymentKey(),
			payment.getPrice(),
			payment.getStatus().name()
		);
	}
}
