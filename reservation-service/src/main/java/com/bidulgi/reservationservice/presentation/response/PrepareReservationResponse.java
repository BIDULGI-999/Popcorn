package com.bidulgi.reservationservice.presentation.response;

import java.util.UUID;

import com.bidulgi.reservationservice.domain.model.Reservation;

public record PrepareReservationResponse(
	UUID id,
	UUID productId,
	UUID reservationSlotId,
	int quantity,
	String visitorName,
	String visitorPhone,
	String status   // PENDING
) {
	public static PrepareReservationResponse from(Reservation reservation) {
		return new PrepareReservationResponse(
			reservation.getId(),
			reservation.getProductId(),
			reservation.getReservationSlotId(),
			reservation.getQuantity(),
			reservation.getVisitorName(),
			reservation.getVisitorPhone(),
			reservation.getStatus().name()
		);
	}
}