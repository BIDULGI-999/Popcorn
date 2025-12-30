package com.bidulgi.placeservice.domain.exception;

import org.springframework.http.HttpStatus;

import com.bidulgi.common.globalException.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceErrorCode {

	PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "장소를 찾을 수 없습니다.", ErrorCode.RESOURCE_NOT_FOUND),
	INVALID_COORDINATES(HttpStatus.BAD_REQUEST, "유효하지 않은 좌표입니다.", ErrorCode.BAD_REQUEST),
	DUPLICATE_AREA_NAME(HttpStatus.BAD_REQUEST, "중복된 지역 이름이 존재합니다.", ErrorCode.BAD_REQUEST),
	AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "지역을 찾을 수 없습니다.", ErrorCode.RESOURCE_NOT_FOUND);

	private final HttpStatus httpStatus;
	private final String message;
	private final ErrorCode errorCode;
}
