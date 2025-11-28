package com.bidulgi.reservationservice.domain.model;

public enum ReservationStatus {
	PENDING,      // 결제 대기 / 임시 예약
	COMPLETED,    // 결제 완료 + 예약 확정
	FAILED,       // 결제 실패
	CANCELED,     // 사용자/관리자 취소
	EXPIRED,      // 시간 만료
	USED          // 실제 방문/사용 완료
}