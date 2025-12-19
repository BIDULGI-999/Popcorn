package com.bidulgi.paymentservice.application.port.out.dto;

public record PaymentConfirmResult(
	String paymentKey,
	String orderId,
	String status,
	String method,
	Integer totalAmount,
	Integer balanceAmount,
	String approvedAt,
	boolean isPartialCancelable
) {
}
