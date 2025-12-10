package com.bidulgi.reservationservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidulgi.reservationservice.domain.model.Reservation;

@Repository
public interface ReservationJpaRepository extends JpaRepository<Reservation, UUID> {
}
