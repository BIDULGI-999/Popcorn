package com.bidulgi.paymentservice.infrastructure.client.config;

import java.io.IOException;
import java.io.InputStream;

import com.bidulgi.paymentservice.domain.exception.PaymentErrorCode;
import com.bidulgi.paymentservice.domain.exception.PaymentException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TossFeignErrorDecoder implements ErrorDecoder {

	private final ObjectMapper mapper = new ObjectMapper();

	// 토스 API 에러 코드
	private static final String ALREADY_PROCESSED_PAYMENT = "ALREADY_PROCESSED_PAYMENT";

	@Override
	public Exception decode(String methodKey, Response response) {
		String code = null;
		String message = null;

		try (InputStream body = response.body() != null ? response.body().asInputStream() : null) {
			if (body != null) {
				JsonNode root = mapper.readTree(body);
				code = root.path("code").asText(null);
				message = root.path("message").asText(null);
			}
		} catch (IOException e) {
			log.warn("Failed to parse Toss error response", e);
		}

		log.error("Toss API error - status: {}, code: {}, message: {}", response.status(), code, message);

		// 이미 승인된 결제
		if (ALREADY_PROCESSED_PAYMENT.equals(code)) {
			return new PaymentException(PaymentErrorCode.ALREADY_APPROVED);
		}

		String detail = message != null ? message : "토스 API 호출 실패 (status=" + response.status() + ")";
		return new PaymentException(PaymentErrorCode.TOSS_API_ERROR, detail);
	}
}
