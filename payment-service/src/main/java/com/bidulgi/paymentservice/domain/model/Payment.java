package com.bidulgi.paymentservice.domain.model;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.bidulgi.common.model.BaseEntity;
import com.bidulgi.paymentservice.domain.exception.PaymentErrorCode;
import com.bidulgi.paymentservice.domain.exception.PaymentException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_payment")
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String paymentKey;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private String orderId;

	private String orderName;

	@Column(nullable = false)
	private Integer price;

	private String method;

	private Integer balanceAmount;

	private boolean isPartialCancelable = false;

	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	private LocalDateTime approvedAt;

	private LocalDateTime canceledAt;

	@Builder
	public Payment(UUID userId, String paymentKey, Integer price, String orderId) {
		this.userId = userId;
		this.paymentKey = paymentKey;
		this.price = price;
		this.balanceAmount = price;
		this.orderId = orderId;
		this.status = PaymentStatus.READY;
	}

	public void approve(String status, String method, LocalDateTime approvedAt, boolean isPartialCancelable) {
		// 상태 검증
		if (!canApprove()) {
			throw new PaymentException(PaymentErrorCode.INVALID_APPROVE);
		}

		if (this.status == PaymentStatus.DONE) {
			throw new PaymentException(PaymentErrorCode.ALREADY_APPROVED);
		}

		this.status = PaymentStatus.valueOf(status);
		this.approvedAt = approvedAt;
		this.isPartialCancelable = isPartialCancelable;
		this.method = method;
	}

	public void cancel(String status, OffsetDateTime canceledAt, Integer cancelAmount) {
		// 1. 상태 검증
		if (isCanceled()) {
			throw new PaymentException(PaymentErrorCode.ALREADY_CANCELED);
		}

		if (!canCancel()) {
			throw new PaymentException(PaymentErrorCode.INVALID_CANCEL);
		}

		// 2. 금액 검증
		validateCancelAmount(cancelAmount);

		// 3. 상태 변경
		this.status = PaymentStatus.valueOf(status);
		this.canceledAt = canceledAt.toLocalDateTime();
		this.balanceAmount -= cancelAmount;
	}

	public boolean canApprove() {
		return this.status == PaymentStatus.READY;
	}

	public boolean canCancel() {
		return this.status == PaymentStatus.DONE ||
			this.status == PaymentStatus.PARTIAL_CANCELED;
	}

	public boolean isCanceled() {
		return this.status == PaymentStatus.CANCELED;
	}

	public void validateCancelAmount(Integer cancelAmount) {
		if (cancelAmount == null || cancelAmount <= 0) {
			throw new PaymentException(PaymentErrorCode.INVALID_CANCEL_AMOUNT);
		}

		if (cancelAmount > this.balanceAmount) {
			throw new PaymentException(PaymentErrorCode.CANCEL_AMOUNT_EXCEEDED);
		}

		if (!this.isPartialCancelable && !cancelAmount.equals(this.balanceAmount)) {
			throw new PaymentException(PaymentErrorCode.PARTIAL_CANCEL_NOT_ALLOWED);
		}
	}

}
