package com.bidulgi.paymentservice.infrastructure.client.dto;

public record CancelTossRequest (
	String cancelReason,
	Integer cancelAmount
){
}
