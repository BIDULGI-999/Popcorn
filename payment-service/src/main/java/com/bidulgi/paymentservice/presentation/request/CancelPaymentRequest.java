package com.bidulgi.paymentservice.presentation.request;

import com.bidulgi.paymentservice.application.dto.CancelPaymentCommand;

public record CancelPaymentRequest(
	String paymentKey,
	String cancelReason,
	Integer cancelAmount
) {
	public CancelPaymentCommand toCommand() {
		return new CancelPaymentCommand(paymentKey,cancelReason,cancelAmount == null ? 0 : cancelAmount);
	}
}
