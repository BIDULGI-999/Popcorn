package com.bidulgi.reservationservice.infrastructure.inbox.payload;

import java.util.UUID;

public record StockDecreaseResultPayload(
	UUID reservationId,
	UUID productId,
	UUID reservationSlotId,
	Integer quantity,
	String message
) {
}
