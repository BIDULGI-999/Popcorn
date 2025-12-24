package com.bidulgi.paymentservice.application.port.out.dto;

public record PaymentCancelRequest(
	String cancelReason,
	Integer cancelAmount
) {
}
