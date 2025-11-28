package com.bidulgi.reservationservice.domain.repository;

import com.bidulgi.reservationservice.domain.model.Reservation;

public interface ReservationRepository {
	void save(Reservation reservation);
}
