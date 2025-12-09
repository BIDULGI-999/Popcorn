package com.bidulgi.paymentservice.presentation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bidulgi.common.globalException.ErrorResponse;
import com.bidulgi.paymentservice.domain.exception.PaymentException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class PaymentExceptionHandler {

	@ExceptionHandler(PaymentException.class)
	public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException ex) {

		log.error("PaymentError:{}", ex.getMessage());

		final ErrorResponse response = ErrorResponse.of(
			ex.getPaymentErrorCode().getErrorCode(),
			ex.getMessage()
		);
		return new ResponseEntity<>(response, ex.getPaymentErrorCode().getHttpStatus());
	}
}
