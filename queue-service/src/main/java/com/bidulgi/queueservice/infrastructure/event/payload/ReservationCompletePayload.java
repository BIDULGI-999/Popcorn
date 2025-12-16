package com.bidulgi.queueservice.infrastructure.event.payload;

import java.util.UUID;

public record ReservationCompletePayload(
	UUID userId,
	UUID productId
) {
}
