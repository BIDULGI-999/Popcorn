package com.bidulgi.paymentservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bidulgi.paymentservice.infrastructure.client.config.TossFeignConfig;
import com.bidulgi.paymentservice.infrastructure.client.dto.CancelTossRequest;
import com.bidulgi.paymentservice.infrastructure.client.dto.TossPaymentResponse;
import com.bidulgi.paymentservice.infrastructure.client.dto.ConfirmTossRequest;
import com.bidulgi.paymentservice.infrastructure.client.dto.ConfirmTossResponse;

@FeignClient(
	name = "tossPaymentsClient",
	url = "${toss.payments.base-url}",
	configuration = TossFeignConfig.class
)
public interface TossPaymentClient {

	@PostMapping("/v1/payments/confirm")
	ConfirmTossResponse confirm(@RequestBody ConfirmTossRequest request);

	@PostMapping("/v1/payments/{paymentKey}/cancel")
	TossPaymentResponse cancel(@RequestBody CancelTossRequest request, @PathVariable String paymentKey);

	@GetMapping("/v1/payments/{paymentKey}")
	TossPaymentResponse getPayment(@PathVariable String paymentKey);
}
