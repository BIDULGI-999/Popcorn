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
@Table(name = "p_compensation_log")
public class CompensationLog {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true)
	private UUID paymentId;

	@Column(nullable = false)
	private String reason;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CompensationStatus status;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	private LocalDateTime completedAt;

	@Builder
	private CompensationLog(UUID paymentId, String reason) {
		this.paymentId = paymentId;
		this.reason = reason;
		this.status = CompensationStatus.PROCESSING;
		this.createdAt = LocalDateTime.now();
	}

	public static CompensationLog create(UUID paymentId, String reason) {
		return CompensationLog.builder()
			.paymentId(paymentId)
			.reason(reason)
			.build();
	}

	public boolean isCompleted() {
		return this.status == CompensationStatus.COMPLETED;
	}

	public boolean isProcessing() {
		return this.status == CompensationStatus.PROCESSING;
	}

	public boolean isTossCanceled() {
		return this.status == CompensationStatus.TOSS_CANCELED;
	}

	public boolean isFailed() {
		return this.status == CompensationStatus.FAILED;
	}

	public void markTossCanceled() {
		validateNotTerminalState();
		if (!isProcessing()) {
			throw new IllegalStateException(
				String.format("TOSS_CANCELED로 전이 불가. 현재 상태: %s", this.status)
			);
		}
		this.status = CompensationStatus.TOSS_CANCELED;
	}

	public void complete() {
		validateNotTerminalState();
		if (!isTossCanceled()) {
			throw new IllegalStateException(
				String.format("COMPLETED로 전이 불가. 현재 상태: %s", this.status)
			);
		}
		this.status = CompensationStatus.COMPLETED;
		this.completedAt = LocalDateTime.now();
	}

	public void fail() {
		validateNotTerminalState();
		this.status = CompensationStatus.FAILED;
	}

	private void validateNotTerminalState() {
		if (isCompleted() || isFailed()) {
			throw new IllegalStateException(
				String.format("이미 최종 상태입니다. 현재 상태: %s", this.status)
			);
		}
	}
}
