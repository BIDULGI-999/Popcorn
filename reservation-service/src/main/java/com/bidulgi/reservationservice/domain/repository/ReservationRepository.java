package com.bidulgi.reservationservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.bidulgi.reservationservice.domain.model.Reservation;

public interface ReservationRepository {
	void save(Reservation reservation);
	Optional<Reservation> findById(UUID id);
}
