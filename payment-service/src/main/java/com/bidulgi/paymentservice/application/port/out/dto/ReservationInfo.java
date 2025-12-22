package com.bidulgi.paymentservice.application.port.out.dto;

import java.util.UUID;

public record ReservationInfo(
	UUID id,
	UUID productId,
	UUID reservationSlotId,
	UUID userId,
	Integer amount,
	Integer quantity,
	String visitorName,
	String visitorPhone,
	String status
) {
}
