package com.bidulgi.paymentservice.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.paymentservice.application.dto.CancelPaymentResponse;
import com.bidulgi.paymentservice.application.dto.ConfirmPaymentResponse;
import com.bidulgi.paymentservice.application.facade.PaymentFacade;
import com.bidulgi.paymentservice.application.service.PaymentService;
import com.bidulgi.paymentservice.presentation.request.CancelPaymentRequest;
import com.bidulgi.paymentservice.presentation.request.CreatePaymentRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentControllerV1 {

	private final PaymentFacade paymentFacade;

	@PostMapping("/confirm")
	public ApiResponse<?> confirmPayment(@AuthenticationPrincipal UserPrincipal user,
		@RequestBody CreatePaymentRequest createPaymentRequest) {

		ConfirmPaymentResponse response = paymentFacade.confirm(createPaymentRequest.toCommand(), user);
		return ApiResponse.success(response, "success");
	}

	@PostMapping("/cancel")
	public ApiResponse<?> cancelPayment(@AuthenticationPrincipal UserPrincipal user,
		@RequestBody CancelPaymentRequest cancelPaymentRequest) {

		CancelPaymentResponse response = paymentFacade.cancel(cancelPaymentRequest.toCommand(), user);
		return ApiResponse.success(response, "success");
	}
}
