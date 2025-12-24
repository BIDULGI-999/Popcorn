package com.bidulgi.paymentservice.infrastructure.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.bidulgi.paymentservice.application.port.out.ReservationReader;
import com.bidulgi.paymentservice.application.port.out.dto.ReservationInfo;
import com.bidulgi.paymentservice.infrastructure.client.dto.ReservationResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationClientAdapter implements ReservationReader {

	private final ReservationClient reservationClient;

	@Override
	public ReservationInfo getReservation(UUID reservationId) {
		ReservationResponse response = reservationClient.getReservationById(reservationId).data();

		return new ReservationInfo(
			response.id(),
			response.productId(),
			response.reservationSlotId(),
			response.userId(),
			response.amount(),
			response.quantity(),
			response.visitorName(),
			response.visitorPhone(),
			response.status()
		);
	}
}
