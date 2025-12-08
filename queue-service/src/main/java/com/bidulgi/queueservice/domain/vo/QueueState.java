package com.bidulgi.queueservice.domain.vo;

import lombok.Getter;

@Getter
public enum QueueState {
	WAIT,
	ACTIVE;

	public static QueueState of(String state) {
		return QueueState.valueOf(state);
	}
}
