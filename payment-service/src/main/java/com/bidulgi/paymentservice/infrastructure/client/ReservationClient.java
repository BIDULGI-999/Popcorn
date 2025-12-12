package com.bidulgi.paymentservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.paymentservice.infrastructure.client.dto.ReservationResponse;

@FeignClient(name = "reservation-service", path = "/internal/reservations")
public interface ReservationClient {

	@GetMapping("/{id}")
	ApiResponse<ReservationResponse> getReservationById(@PathVariable("id") UUID id);
}
