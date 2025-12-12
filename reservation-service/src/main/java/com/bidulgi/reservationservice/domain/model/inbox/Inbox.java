package com.bidulgi.reservationservice.domain.model.inbox;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_inbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Inbox {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "event_id", nullable = false, unique = true, length = 64)
	private String eventId;

	@Column(name = "event_type", nullable = false, length = 32)
	private String eventType;

	@Lob
	@Column(name = "payload", nullable = false)
	private String payload;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private InboxStatus status;

	@Column(name = "try_count", nullable = false)
	private int tryCount;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "processed_at")
	private LocalDateTime processedAt;

	@Builder
	public Inbox(String eventId, String eventType, String payload, InboxStatus status, int tryCount, LocalDateTime createdAt) {
		this.eventId = eventId;
		this.eventType = eventType;
		this.payload = payload;
		this.status = status;
		this.tryCount = tryCount;
		this.createdAt = createdAt;
	}

	public static Inbox create(String eventId, String eventType, String payload) {
		return Inbox.builder()
			.eventId(eventId)
			.eventType(eventType)
			.payload(payload)
			.status(InboxStatus.PENDING)
			.tryCount(0)
			.createdAt(LocalDateTime.now())
			.build();
	}

	public void markProcessed() {
		this.status = InboxStatus.PROCESSED;
		this.processedAt = LocalDateTime.now();
	}

	public void incrementTry() {
		this.tryCount++;
		if (this.tryCount > 5) {
			this.status = InboxStatus.DEAD;
		}
	}
}