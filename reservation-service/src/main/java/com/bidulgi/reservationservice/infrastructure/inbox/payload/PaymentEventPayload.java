package com.bidulgi.reservationservice.infrastructure.inbox.payload;

import java.util.UUID;

public record PaymentEventPayload(
	UUID paymentId,
	int paidAmount
) {}