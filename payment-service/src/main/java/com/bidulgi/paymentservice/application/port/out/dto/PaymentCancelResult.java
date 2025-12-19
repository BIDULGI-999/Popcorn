package com.bidulgi.paymentservice.application.port.out.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record PaymentCancelResult(
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
		OffsetDateTime canceledAt
	) {}

	public CancelDetail getLatestCancel() {
		if (cancels == null || cancels.isEmpty()) {
			return null;
		}
		return cancels.get(cancels.size() - 1);
	}
}
