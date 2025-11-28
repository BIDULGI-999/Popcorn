package com.bidulgi.reservationservice.application.service;

import org.springframework.stereotype.Service;

import com.bidulgi.reservationservice.domain.model.Reservation;
import com.bidulgi.reservationservice.domain.repository.ReservationRepository;
import com.bidulgi.reservationservice.presentation.request.CreateReservationRequest;
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
}
