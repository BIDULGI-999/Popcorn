package com.bidulgi.paymentservice.application.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.bidulgi.paymentservice.application.port.out.dto.PaymentCancelResult;

public record ApplyCancelCommand(
	String status,           // CANCELED or PARTIAL_CANCELED
	OffsetDateTime canceledAt,
	Integer cancelAmount,    // 이번에 취소된 금액
	Integer balanceAmount    // 취소 후 남은 잔액
) {
	public static ApplyCancelCommand from(PaymentCancelResult result) {
		PaymentCancelResult.CancelDetail cd = result.getLatestCancel();

		return new ApplyCancelCommand(
			result.status(),
			cd.canceledAt(),
			cd.cancelAmount(),
			result.balanceAmount()
		);
	}
}
