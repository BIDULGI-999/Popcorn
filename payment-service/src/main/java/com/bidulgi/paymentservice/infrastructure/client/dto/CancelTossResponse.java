package com.bidulgi.paymentservice.infrastructure.client.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CancelTossResponse(
	String paymentKey,
	String orderId,
	String status,
	Integer balanceAmount,
	boolean isPartialCancelable,
	List<CancelDetail> cancels
) {
	public record CancelDetail(
		String transactionKey,
		String cancelReason,
		Integer cancelAmount,
		Integer refundableAmount,
		String cancelStatus,
		LocalDateTime canceledAt
	) {}

	// 가장 최근 취소 내역 가져오기
	public CancelDetail getLatestCancel() {
		if (cancels == null || cancels.isEmpty()) {
			return null;
		}
		return cancels.get(cancels.size() - 1);
	}
}
