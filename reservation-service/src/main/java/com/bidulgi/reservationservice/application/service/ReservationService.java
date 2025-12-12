package com.bidulgi.reservationservice.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bidulgi.common.globalException.custom.EntityNotFoundException;
import com.bidulgi.common.globalException.custom.InternalServiceException;
import com.bidulgi.common.response.PageResponse;
import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.reservationservice.domain.model.Reservation;
import com.bidulgi.reservationservice.domain.model.ReservationStatus;
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

	public PageResponse<ReservationResponse> getReservations(
		UUID userId,
		ReservationStatus status,
		int page,
		int size
	) {
		Pageable pageable = PageRequest.of(
			page,
			size,
			Sort.by(Sort.Direction.DESC, "createdAt")
		);

		Page<Reservation> result;
		if (status == null) {
			result = reservationRepository.findByUserId(userId, pageable);
		} else {
			result = reservationRepository.findByUserIdAndStatus(userId, status, pageable);
		}

		Page<ReservationResponse> mapped = result.map(ReservationResponse::from);
		return PageResponse.of(mapped);
	}

	public ReservationResponse getReservationDetail(UserPrincipal userPrincipal, UUID reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new EntityNotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));

		boolean isMaster = userPrincipal.isMaster();

		if (!isMaster) {
			if (reservation.getUserId() != null && !reservation.getUserId().equals(userPrincipal.id())) {
				throw new InternalServiceException("현재 사용자와 예약 소유자가 일치하지 않습니다.");
			}
		}
		// Todo: 상품쪽 Internal 상세 조회 후 같이 반환 필요(팝업 정보랑 그 회차랑 예약한 회차까지)
		return ReservationResponse.from(reservation);
	}
}
