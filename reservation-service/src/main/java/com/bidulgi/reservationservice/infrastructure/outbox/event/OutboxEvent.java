package com.bidulgi.reservationservice.infrastructure.outbox.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent<T> {
	private String aggregateType;
	private String aggregateId;
	private String eventType;
	private T payload;
	private String eventId;

}