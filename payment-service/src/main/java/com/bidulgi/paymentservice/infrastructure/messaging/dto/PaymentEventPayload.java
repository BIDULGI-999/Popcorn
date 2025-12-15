package com.bidulgi.paymentservice.infrastructure.messaging.dto;

import java.util.UUID;

public record PaymentEventPayload(
	UUID paymentId,
	int paidAmount
) {}
