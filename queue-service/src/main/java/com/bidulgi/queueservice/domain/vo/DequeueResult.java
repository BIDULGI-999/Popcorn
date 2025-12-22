package com.bidulgi.queueservice.domain.vo;

import java.util.UUID;

public record DequeueResult(
	UUID userId,
	UUID productId
) {
}
