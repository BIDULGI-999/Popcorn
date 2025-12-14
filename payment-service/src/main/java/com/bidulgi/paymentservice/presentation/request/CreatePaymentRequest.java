package com.bidulgi.paymentservice.presentation.request;

import com.bidulgi.paymentservice.application.dto.ConfirmPaymentCommand;

public record CreatePaymentRequest(
	String orderId,
	Integer amount,
	String paymentKey
) {
	public ConfirmPaymentCommand toCommand(){
		return new ConfirmPaymentCommand(orderId, paymentKey, amount);
	}
}
