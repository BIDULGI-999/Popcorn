package com.bidulgi.reservationservice.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.bidulgi.reservationservice.domain.model.Reservation;
import com.bidulgi.reservationservice.domain.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

	private final ReservationJpaRepository jpaRepository;

	@Override
	public void save(Reservation reservation) {
		jpaRepository.save(reservation);
	}
}
