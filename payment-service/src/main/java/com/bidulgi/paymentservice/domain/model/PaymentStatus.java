package com.bidulgi.paymentservice.domain.model;

public enum PaymentStatus {
	READY,              // 결제 생성, 인증 전
	IN_PROGRESS,        // 인증 완료, 승인 대기
	DONE,               // 결제 승인 완료
	CANCELED,           // 전액 취소
	PARTIAL_CANCELED,   // 부분 취소
	ABORTED,            // 결제 승인 실패
	EXPIRED             // 유효시간 만료 (30분)
}
