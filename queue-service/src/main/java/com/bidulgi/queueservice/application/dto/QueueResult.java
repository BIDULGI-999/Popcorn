package com.bidulgi.queueservice.application.dto;

import java.util.UUID;

import com.bidulgi.queueservice.domain.vo.QueueState;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record QueueResult(
	UUID userId,
	UUID productId,
	String state,
	String token
) {
	public static QueueResult of(UUID userId, UUID productId, QueueState state, String token) {
		return new QueueResult(userId, productId, state.name(), token);
	}

	public static QueueResult of(UUID userId, UUID productId, QueueState state) {
		return new QueueResult(userId, productId, state.name(), null);
	}
}
