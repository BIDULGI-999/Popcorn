package com.bidulgi.paymentservice.presentation.controller;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidulgi.paymentservice.application.service.PaymentCompensationService;

import lombok.RequiredArgsConstructor;

@Profile("!prod")
@RestController
@RequiredArgsConstructor
@RequestMapping("/test/payments")
public class PaymentTestController {

	private final PaymentCompensationService compensationService;

	@PostMapping("/{paymentId}/compensate")
	public ResponseEntity<String> testCompensation(@PathVariable UUID paymentId) {
		compensationService.compensatePayment(paymentId);
		return ResponseEntity.ok("보상 트랜잭션 실행 완료: " + paymentId);
	}
}
