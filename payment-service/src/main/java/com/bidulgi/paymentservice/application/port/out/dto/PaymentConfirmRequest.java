package com.bidulgi.paymentservice.application.port.out.dto;

public record PaymentConfirmRequest(
	String paymentKey,
	String orderId,
	int amount
) {
}
