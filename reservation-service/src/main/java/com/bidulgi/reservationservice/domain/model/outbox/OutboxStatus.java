package com.bidulgi.reservationservice.domain.model.outbox;

import lombok.Getter;

@Getter
public enum OutboxStatus {
	PENDING, SUCCESS, FAILED
}
