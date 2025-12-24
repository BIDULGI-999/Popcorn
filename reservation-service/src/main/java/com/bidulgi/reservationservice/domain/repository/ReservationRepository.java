package com.bidulgi.reservationservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bidulgi.reservationservice.domain.model.Reservation;
import com.bidulgi.reservationservice.domain.model.ReservationStatus;

public interface ReservationRepository {
	void save(Reservation reservation);
	Optional<Reservation> findById(UUID id);
	Page<Reservation> findByUserId(UUID userId, Pageable pageable);
	Page<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status, Pageable pageable);
}
