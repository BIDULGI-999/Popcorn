package com.bidulgi.reservationservice.infrastructure.outbox.event;

import com.bidulgi.reservationservice.infrastructure.outbox.payload.StockDecreasePayload;

public class StockDecreaseRequestedEvent extends OutboxEvent {
	public StockDecreaseRequestedEvent(String aggregateId, StockDecreasePayload payload) {
		super("product", aggregateId, "STOCK_DECREASE_REQUESTED", payload, "");
	}
}
