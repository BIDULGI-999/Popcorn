package com.bidulgi.queueservice.domain.event;

import java.util.UUID;

public record ReservationCompletedEvent(
	UUID userId,
	UUID productId
) {
	public static ReservationCompletedEvent of(UUID userId, UUID productId) {
		return new ReservationCompletedEvent(
			userId,
			productId
		);
	}
}
