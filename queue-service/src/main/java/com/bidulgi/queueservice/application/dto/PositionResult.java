package com.bidulgi.queueservice.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PositionResult(
	Long position,
	String token
) {
	public static PositionResult of(Long position) {
		return new PositionResult(position, null);
	}

	public static PositionResult of(String token) {
		return new PositionResult(null, token);
	}
}
