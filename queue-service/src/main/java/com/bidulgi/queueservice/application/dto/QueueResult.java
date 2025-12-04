package com.bidulgi.queueservice.application.dto;

import com.bidulgi.queueservice.domain.vo.QueueState;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record QueueResult(
	String userId,
	String productId,
	String state,
	String token
) {
	public static QueueResult of(String userId, String productId, QueueState state, String token) {
		return new QueueResult(userId, productId, state.name(), token);
	}

	public static QueueResult of(String userId, String productId, QueueState state) {
		return new QueueResult(userId, productId, state.name(), null);
	}
}
