package com.bidulgi.reservationservice.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.common.response.PageResponse;
import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.reservationservice.application.service.ReservationService;
import com.bidulgi.reservationservice.domain.model.ReservationStatus;
import com.bidulgi.reservationservice.presentation.request.PrepareReservationRequest;
import com.bidulgi.reservationservice.presentation.response.PrepareReservationResponse;
import com.bidulgi.reservationservice.presentation.response.ReservationResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

	private final ReservationService reservationService;

	@PostMapping("/prepare/{id}")
	public ResponseEntity<ApiResponse<PrepareReservationResponse>> prepare(@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable UUID id, @RequestBody PrepareReservationRequest request) {
		PrepareReservationResponse response = reservationService.prepare(userPrincipal, id, request);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> getReservations(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) ReservationStatus status
	) {
		PageResponse<ReservationResponse> response =
			reservationService.getReservations(userPrincipal.id(), status, page, size);
		return ResponseEntity.ok(ApiResponse.success(response));
	}
}