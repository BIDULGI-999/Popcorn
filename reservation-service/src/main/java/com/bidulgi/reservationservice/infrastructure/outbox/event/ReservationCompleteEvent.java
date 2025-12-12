package com.bidulgi.reservationservice.infrastructure.outbox.event;

import com.bidulgi.reservationservice.infrastructure.outbox.payload.ReservationCompletePayload;

public class ReservationCompleteEvent extends OutboxEvent {
	public ReservationCompleteEvent(String aggregateId, ReservationCompletePayload payload) {
		super("reservation", aggregateId, "reservationComplete", payload, "");
	}
}
