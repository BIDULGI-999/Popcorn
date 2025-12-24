package com.bidulgi.queueservice.domain.event;

import java.util.UUID;

public record TokenExpiredEvent(
	UUID userId,
	UUID productId
) {
	public static TokenExpiredEvent of(String userId, String productId) {
		return new TokenExpiredEvent(
			UUID.fromString(userId),
			UUID.fromString(productId)
		);
	}
}
