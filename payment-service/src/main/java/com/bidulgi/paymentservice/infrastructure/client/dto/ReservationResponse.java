package com.bidulgi.paymentservice.infrastructure.client.dto;

import java.util.UUID;

public record ReservationResponse(
	UUID reservationId,
	String orderId,
	Integer amount,
	String status
) {
}
