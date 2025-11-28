package com.bidulgi.reservationservice.presentation.response;

import java.util.UUID;

import com.bidulgi.reservationservice.domain.model.Reservation;

public record ReservationResponse(
	UUID id,
	UUID productId,
	UUID reservationSlotId,
	UUID userId,
	int quantity,
	String phone,
	String status
) {
	public static ReservationResponse from(Reservation reservation) {
		return new ReservationResponse(
			reservation.getId(),
			reservation.getProductId(),
			reservation.getReservationSlotId(),
			reservation.getUserId(),
			reservation.getQuantity(),
			reservation.getPhone(),
			reservation.getStatus().name()
		);
	}
}