package com.bidulgi.reservationservice.presentation.response;

import java.util.UUID;

import com.bidulgi.reservationservice.domain.model.Reservation;

public record ReservationResponse(
	UUID id,
	UUID productId,
	UUID reservationSlotId,
	UUID userId,
	Integer amount,
	Integer quantity,
	String visitorName,
	String visitorPhone,
	String status
) {
	public static ReservationResponse from(Reservation reservation) {
		return new ReservationResponse(
			reservation.getId(),
			reservation.getProductId(),
			reservation.getReservationSlotId(),
			reservation.getUserId(),
			reservation.getAmount(),
			reservation.getQuantity(),
			reservation.getVisitorName(),
			reservation.getVisitorPhone(),
			reservation.getStatus().name()
		);
	}
}