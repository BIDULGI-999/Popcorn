package com.bidulgi.reservationservice.domain.model;

import java.util.UUID;

import com.bidulgi.common.model.BaseEntity;
import com.bidulgi.reservationservice.presentation.request.CreateReservationRequest;
import com.bidulgi.reservationservice.presentation.request.PrepareReservationRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

	@Id
	@Column(columnDefinition = "UUID")
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "product_id", nullable = false, columnDefinition = "UUID")
	private UUID productId;

	@Column(name = "reservation_slot_id", nullable = false, columnDefinition = "UUID")
	private UUID reservationSlotId;

	@Column(name = "payment_id", columnDefinition = "UUID")
	private UUID paymentId;

	@Column(name = "amount")
	private Integer amount;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "visitor_name", length = 20)
	private String visitorName;

	@Column(name = "visitor_phone", length = 20)
	private String visitorPhone;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private ReservationStatus status;

	@Column(name = "qr_code")
	private String qrCode;

	@Builder
	public Reservation(
		UUID id,
		UUID userId,
		UUID productId,
		UUID reservationSlotId,
		UUID paymentId,
		Integer amount,
		Integer quantity,
		String visitorName,
		String visitorPhone,
		ReservationStatus status,
		String qrCode
	) {
		this.id = id;
		this.userId = userId;
		this.productId = productId;
		this.reservationSlotId = reservationSlotId;
		this.paymentId = paymentId;
		this.amount = amount;
		this.quantity = quantity;
		this.visitorName = visitorName;
		this.visitorPhone = visitorPhone;
		this.status = status;
		this.qrCode = qrCode;
	}

	public static Reservation createRequested(UUID userId, CreateReservationRequest request) {
		return Reservation.builder()
			.id(UUID.randomUUID())
			.userId(userId)
			.productId(request.productId())
			.reservationSlotId(request.reservationSlotId())
			.amount(request.amount())
			.quantity(request.quantity())
			.status(ReservationStatus.REQUESTED)
			.build();
	}

	public void updateVisitorInfo(PrepareReservationRequest request) {
		this.visitorName = request.visitorName();
		this.visitorPhone = request.visitorPhone();
	}

	public void prepare() {
		if (this.amount == null || this.amount == 0) {
			this.status = ReservationStatus.COMPLETED;
		} else {
			this.status = ReservationStatus.PENDING;
		}
	}

	public boolean isPending() {
		return this.status == ReservationStatus.PENDING;
	}

	public void complete(UUID paymentId) {
		this.status = ReservationStatus.COMPLETED;
		this.paymentId = paymentId;
	}

	public boolean canCancel() {
		return this.status == ReservationStatus.COMPLETED;
	}

	public boolean isCanceled() {
		return this.status == ReservationStatus.CANCELED;
	}

	public void cancel() {
		this.status = ReservationStatus.CANCELED;
	}

	public void use() {
		this.status = ReservationStatus.USED;
	}

	public void markHold() {
		this.status = ReservationStatus.HOLD;
	}

	public void markFailed() {
		this.status = ReservationStatus.FAILED;
	}
}
