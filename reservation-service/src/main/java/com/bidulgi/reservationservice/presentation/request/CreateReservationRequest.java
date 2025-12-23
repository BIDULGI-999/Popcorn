package com.bidulgi.reservationservice.presentation.request;

import java.util.UUID;

public record CreateReservationRequest(
	UUID reservationSlotId,
	UUID productId,
	Integer amount,
	Integer quantity
) {}