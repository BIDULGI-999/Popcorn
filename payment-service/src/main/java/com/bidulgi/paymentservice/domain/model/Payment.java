package com.bidulgi.paymentservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

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
public class Payment {

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
		this.status = PaymentStatus.valueOf(status);
		this.approvedAt = approvedAt;
		this.isPartialCancelable = isPartialCancelable;
		this.method = method;
	}

	// TODO: 결제 취소, 부분 취소
	public void cancel(String status, String method, LocalDateTime canceledAt, boolean isPartialCancelable) {
	}
}
