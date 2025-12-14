package com.bidulgi.paymentservice.application.dto;

public record CancelPaymentCommand (
	String paymentKey,
	String cancelReason,
	Integer cancelAmount
){
}
