package com.bidulgi.paymentservice.application.dto;

public record ConfirmPaymentCommand (
	String orderId,
	String paymentKey,
	Integer amount
){
}
