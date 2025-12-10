package com.bidulgi.paymentservice.domain.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

	private PaymentErrorCode paymentErrorCode;

	public PaymentException(PaymentErrorCode paymentErrorCode) {
		super(paymentErrorCode.getMessage());
		this.paymentErrorCode = paymentErrorCode;
	}

	public PaymentException(PaymentErrorCode paymentErrorCode, String detail) {
		super(paymentErrorCode.getMessage() + "-" + detail);
		this.paymentErrorCode = paymentErrorCode;
	}

	public PaymentException(PaymentErrorCode paymentErrorCode, Throwable cause) {
		super(paymentErrorCode.getMessage(), cause);
		this.paymentErrorCode = paymentErrorCode;
	}
}
