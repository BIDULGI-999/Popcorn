package com.bidulgi.paymentservice.application.dto;

import java.time.LocalDateTime;

import com.bidulgi.paymentservice.infrastructure.client.dto.CancelTossResponse;

public record ApplyCancelCommand(
	String status,           // CANCELED or PARTIAL_CANCELED
	LocalDateTime canceledAt,
	Integer cancelAmount,    // 이번에 취소된 금액
	Integer balanceAmount    // 취소 후 남은 잔액
) {
	public static ApplyCancelCommand from(CancelTossResponse response) {
		CancelTossResponse.CancelDetail cd = response.getLatestCancel();

		return new ApplyCancelCommand(
			response.status(),
			cd.canceledAt(),
			cd.cancelAmount(),
			response.balanceAmount()
		);
	}
}
