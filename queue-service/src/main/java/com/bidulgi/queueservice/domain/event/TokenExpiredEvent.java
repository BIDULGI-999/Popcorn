package com.bidulgi.queueservice.domain.event;

public record TokenExpiredEvent(
	String userId,
	String productId
) {}
