package com.bidulgi.paymentservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bidulgi.paymentservice.infrastructure.client.config.TossFeignConfig;
import com.bidulgi.paymentservice.infrastructure.client.dto.CancelTossRequest;
import com.bidulgi.paymentservice.infrastructure.client.dto.CancelTossResponse;
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
	CancelTossResponse cancel(@RequestBody CancelTossRequest request, @PathVariable String paymentKey);
}
