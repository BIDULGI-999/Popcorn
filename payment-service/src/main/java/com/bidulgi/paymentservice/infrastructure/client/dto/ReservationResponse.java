package com.bidulgi.paymentservice.infrastructure.client.dto;

import java.util.UUID;

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
}
