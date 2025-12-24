package com.bidulgi.queueservice.presentation.dto.response;

import com.bidulgi.queueservice.application.dto.PositionResult;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PositionResponse(
	Long position,
	String token
) {
	public static PositionResponse from(PositionResult result) {
		return new PositionResponse(
			result.position(),
			result.token()
		);
	}
}
