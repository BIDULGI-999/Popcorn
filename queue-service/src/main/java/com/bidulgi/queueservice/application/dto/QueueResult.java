package com.bidulgi.queueservice.application.dto;

import com.bidulgi.queueservice.domain.model.QueueState;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record QueueResult(
	String productId,
	String state,
	String token
) {
	public static QueueResult of(String productId, QueueState state, String token) {
		return new QueueResult(productId, state.name(), token);
	}

	public static QueueResult of(String productId, QueueState state) {
		return new QueueResult(productId, state.name(), null);
	}
}
