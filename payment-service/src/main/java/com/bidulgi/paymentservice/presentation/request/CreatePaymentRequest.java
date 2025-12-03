package com.bidulgi.paymentservice.presentation.request;

import com.bidulgi.paymentservice.domain.model.Payment;

public record CreatePaymentRequest(
	String orderId,
	Integer amount,
	String paymentKey
) {
}
