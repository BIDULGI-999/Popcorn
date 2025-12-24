package com.bidulgi.paymentservice.application.port.out;

import java.util.UUID;

import com.bidulgi.paymentservice.application.port.out.dto.ReservationInfo;

public interface ReservationReader {

	ReservationInfo getReservation(UUID reservationId);
}
