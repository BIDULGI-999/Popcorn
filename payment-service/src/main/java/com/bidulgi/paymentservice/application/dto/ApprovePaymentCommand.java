package com.bidulgi.paymentservice.application.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.bidulgi.paymentservice.application.port.out.dto.PaymentConfirmResult;

public record ApprovePaymentCommand(
	String status,
	String method,
	LocalDateTime approvedAt,
	boolean isPartialCancelable
) {
	public static ApprovePaymentCommand from(PaymentConfirmResult result) {
		return new ApprovePaymentCommand(
			result.status(),
			result.method(),
			OffsetDateTime.parse(result.approvedAt()).toLocalDateTime(),
			result.isPartialCancelable()
		);
	}
}
