package com.bidulgi.paymentservice.application.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.bidulgi.paymentservice.infrastructure.client.dto.ConfirmTossResponse;

public record ApprovePaymentCommand(
	String status,
	String method,
	LocalDateTime approvedAt,
	boolean isPartialCancelable
) {
	public static ApprovePaymentCommand from(ConfirmTossResponse response) {
		return new ApprovePaymentCommand(
			response.status(),
			response.method(),
			OffsetDateTime.parse(response.approvedAt()).toLocalDateTime(),
			response.isPartialCancelable()
		);
	}
}
