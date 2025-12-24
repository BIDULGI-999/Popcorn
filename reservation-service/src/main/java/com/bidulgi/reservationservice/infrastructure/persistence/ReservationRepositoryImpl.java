package com.bidulgi.reservationservice.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bidulgi.reservationservice.domain.model.Reservation;
import com.bidulgi.reservationservice.domain.model.ReservationStatus;
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

	@Override
	public Optional<Reservation> findById(UUID id) {
		return jpaRepository.findById(id);
	}

	@Override
	public Page<Reservation> findByUserId(UUID userId, Pageable pageable) {
		return  jpaRepository.findByUserId(userId,  pageable);
	}

	@Override
	public Page<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status, Pageable pageable) {
		return jpaRepository.findByUserIdAndStatus(userId,  status,  pageable);
	}
}
