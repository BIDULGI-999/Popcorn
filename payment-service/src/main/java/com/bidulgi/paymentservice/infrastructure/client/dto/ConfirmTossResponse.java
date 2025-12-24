package com.bidulgi.paymentservice.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConfirmTossResponse(
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
