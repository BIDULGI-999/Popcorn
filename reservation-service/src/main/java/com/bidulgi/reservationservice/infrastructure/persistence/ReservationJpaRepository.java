package com.bidulgi.reservationservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidulgi.reservationservice.domain.model.Reservation;
import com.bidulgi.reservationservice.domain.model.ReservationStatus;

@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation, UUID> {
	Page<Reservation> findByUserId(UUID userId, Pageable pageable);
	Page<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status, Pageable pageable);
}
