package com.bidulgi.reservationservice.infrastructure.outbox.payload;

import java.util.UUID;

import com.bidulgi.reservationservice.domain.model.Reservation;

public record ReservationCompletePayload(
	UUID userId,
	UUID productId
) {
	public static ReservationCompletePayload from(Reservation reservation) {
		return new ReservationCompletePayload(
			reservation.getUserId(),
			reservation.getProductId()
		);
	}
}
