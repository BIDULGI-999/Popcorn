package com.bidulgi.reservationservice.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bidulgi.common.globalException.custom.EntityNotFoundException;
import com.bidulgi.common.globalException.custom.InternalServiceException;
import com.bidulgi.common.globalException.custom.ServiceException;
import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.reservationservice.domain.model.Reservation;
import com.bidulgi.reservationservice.domain.repository.ReservationRepository;
import com.bidulgi.reservationservice.presentation.request.CreateReservationRequest;
import com.bidulgi.reservationservice.presentation.request.PrepareReservationRequest;
import com.bidulgi.reservationservice.presentation.response.PrepareReservationResponse;
import com.bidulgi.reservationservice.presentation.response.ReservationResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;

	@Transactional
	public ReservationResponse createHoldReservation(CreateReservationRequest request) {
		Reservation reservation = Reservation.createHold(request);
		reservationRepository.save(reservation);
		return ReservationResponse.from(reservation);
	}

	@Transactional
	public PrepareReservationResponse prepare(
		UserPrincipal userPrincipal,
		UUID id,
		PrepareReservationRequest request
	) {
		Reservation reservation = reservationRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("에약을 찾을 수 없습니다. id=" + id));

		if (reservation.getUserId() != null && userPrincipal != null
			&& !reservation.getUserId().equals(userPrincipal.id())) {
			throw new InternalServiceException("현재 사용자와 예약 소유자가 일치하지 않습니다.");
		}

		reservation.updateVisitorInfo(request);
		reservation.prepare();

		reservationRepository.save(reservation);
		return PrepareReservationResponse.from(reservation);
	}
}
