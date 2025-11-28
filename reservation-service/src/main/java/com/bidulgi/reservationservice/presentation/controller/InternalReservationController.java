package com.bidulgi.reservationservice.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.reservationservice.application.service.ReservationService;
import com.bidulgi.reservationservice.presentation.request.CreateReservationRequest;
import com.bidulgi.reservationservice.presentation.response.ReservationResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/reservations")
public class InternalReservationController {

	private final ReservationService reservationService;

	@PostMapping
	public ResponseEntity<ApiResponse<ReservationResponse>> create(@RequestBody CreateReservationRequest request) {
		ReservationResponse response = reservationService.createHoldReservation(request);
		return ResponseEntity.ok(ApiResponse.success(response));
	}
}
