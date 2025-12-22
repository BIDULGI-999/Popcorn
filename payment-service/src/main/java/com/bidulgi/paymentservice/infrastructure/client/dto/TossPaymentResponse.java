package com.bidulgi.paymentservice.infrastructure.client.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentResponse(
	String paymentKey,
	String orderId,
	String status,
	Integer balanceAmount,
	boolean isPartialCancelable,
	List<CancelDetail> cancels
) {
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record CancelDetail(
		String transactionKey,
		String cancelReason,
		Integer cancelAmount,
		Integer refundableAmount,
		String cancelStatus,
		OffsetDateTime canceledAt
	) {}

	// 가장 최근 취소 내역 가져오기
	public CancelDetail getLatestCancel() {
		if (cancels == null || cancels.isEmpty()) {
			return null;
		}
		return cancels.get(cancels.size() - 1);
	}
}
