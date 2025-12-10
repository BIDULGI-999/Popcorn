package com.bidulgi.paymentservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bidulgi.paymentservice.infrastructure.client.dto.ReservationResponse;

@FeignClient(name = "reservation-service", path = "/v1/reservations")
public interface ReservationClient {

	@GetMapping("/validates/{orderId}")
	ReservationResponse getReservationById(@PathVariable("orderId") UUID orderId);
}
