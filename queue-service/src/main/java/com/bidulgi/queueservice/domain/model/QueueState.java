package com.bidulgi.queueservice.domain.model;

import lombok.Getter;

@Getter
public enum QueueState {
	WAIT,
	ACTIVE;

	public static QueueState of(String state) {
		return QueueState.valueOf(state);
	}
}
