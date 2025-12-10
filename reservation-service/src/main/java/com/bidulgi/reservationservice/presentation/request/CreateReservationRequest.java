package com.bidulgi.reservationservice.presentation.request;

import java.util.UUID;

public record CreateReservationRequest(
	UUID userId,
	UUID reservationSlotId,
	UUID productId,
	Integer amount,
	Integer quantity
) {}