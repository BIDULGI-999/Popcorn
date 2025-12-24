package com.bidulgi.reservationservice.infrastructure.outbox.payload;

import java.util.UUID;

import com.bidulgi.reservationservice.domain.model.Reservation;

public record StockDecreasePayload(
	UUID reservationId,
	UUID productId,
	UUID reservationSlotId,
	Integer quantity
) {
	public static StockDecreasePayload from(Reservation reservation) {
		return new StockDecreasePayload(
			reservation.getId(),
			reservation.getProductId(),
			reservation.getReservationSlotId(),
			reservation.getQuantity()
		);
	}
}
