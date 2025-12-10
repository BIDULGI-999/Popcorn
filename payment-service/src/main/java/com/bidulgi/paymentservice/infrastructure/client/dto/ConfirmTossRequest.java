package com.bidulgi.paymentservice.infrastructure.client.dto;

public record ConfirmTossRequest(
	String paymentKey,
	String orderId,
	int amount
) {
}
