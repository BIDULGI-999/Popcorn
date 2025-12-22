package com.bidulgi.paymentservice.domain.exception;

import org.springframework.http.HttpStatus;

import com.bidulgi.common.globalException.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode {

	// 검증 관련
	AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 예약 금액과 일치하지 않습니다.",
		ErrorCode.BAD_REQUEST),
	INVALID_PAYMENT_KEY(HttpStatus.BAD_REQUEST, "유효하지 않은 결제 키입니다.", ErrorCode.BAD_REQUEST),
	INVALID_ORDER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 ID입니다.", ErrorCode.BAD_REQUEST),
	INVALID_CANCEL(HttpStatus.BAD_REQUEST, "유효하지 않은 취소 주문입니다.", ErrorCode.BAD_REQUEST),
	CANCEL_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "취소 금액이 전체 금액보다 큽니다.", ErrorCode.BAD_REQUEST),
	// 상태 관련
	ALREADY_APPROVED(HttpStatus.CONFLICT, "이미 승인된 결제입니다.", ErrorCode.BAD_REQUEST),
	ALREADY_CANCELED(HttpStatus.CONFLICT, "이미 취소된 결제입니다.", ErrorCode.BAD_REQUEST),
	PAYMENT_EXPIRED(HttpStatus.BAD_REQUEST, "결제 유효시간이 만료되었습니다.", ErrorCode.BAD_REQUEST),

	// 토스 API 관련
	TOSS_API_ERROR(HttpStatus.BAD_GATEWAY, "토스 결제 API 호출 중 오류가 발생했습니다.",
		ErrorCode.EXTERNAL_SERVER_ERROR),
	TOSS_CONFIRM_FAILED(HttpStatus.BAD_GATEWAY, "결제 승인에 실패했습니다.",
		ErrorCode.EXTERNAL_SERVER_ERROR),

	// 조회 관련
	PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다.", ErrorCode.RESOURCE_NOT_FOUND);

	private final HttpStatus httpStatus;
	private final String message;
	private final ErrorCode errorCode;
}
