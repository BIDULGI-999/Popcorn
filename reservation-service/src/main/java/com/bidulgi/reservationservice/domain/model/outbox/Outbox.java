package com.bidulgi.reservationservice.domain.model.outbox;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "outbox", uniqueConstraints = @UniqueConstraint(columnNames = "eventId"))
public class Outbox {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private String eventId; // 외부 메시지 식별자 (UUID) - AFTER_COMMIT 단계에서 Outbox 조회에 사용

	@Column(nullable = false)
	private String aggregateType;

	@Column(nullable = false)
	private String aggregateId;

	@Column(nullable = false)
	private String eventType;

	@Column(length = 500, nullable = false)
	private String payload;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OutboxStatus status;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@Builder
	private Outbox(String eventId, String aggregateType, String aggregateId, String eventType, String payload, OutboxStatus status) {
		this.eventId = eventId;
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.eventType = eventType;
		this.payload = payload;
		this.status = status;
	}

	public void markSuccess() {
		this.status = OutboxStatus.SUCCESS;
	}

	public void markFailed() {
		this.status = OutboxStatus.FAILED;
	}

}
