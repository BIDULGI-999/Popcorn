package com.bidulgi.queueservice.presentation.dto.response;

import java.util.Queue;

import com.bidulgi.queueservice.application.dto.QueueResult;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EnqueueResponse(
	String productId,
	String state,
	String token
) {
	public static EnqueueResponse from(QueueResult result) {
		return new EnqueueResponse(
			result.productId(),
			result.state(),
			result.token()
		);
	}
}
