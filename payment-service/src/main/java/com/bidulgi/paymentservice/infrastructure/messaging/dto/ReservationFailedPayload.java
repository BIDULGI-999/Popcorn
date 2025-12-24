package com.bidulgi.paymentservice.infrastructure.messaging.dto;

import java.util.UUID;

public record ReservationFailedPayload(
	String eventType,
	UUID reservationId,
	UUID paymentId,
	String reason
) {
}
