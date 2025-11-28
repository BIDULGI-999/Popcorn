package com.bidulgi.reservationservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidulgi.reservationservice.domain.model.Reservation;

public interface ReservationJpaRepository extends JpaRepository<Reservation, UUID> {
}
