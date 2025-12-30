package com.bidulgi.placeservice.presentation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bidulgi.common.globalException.ErrorResponse;
import com.bidulgi.placeservice.domain.exception.PlaceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class PlaceExceptionHandler {

	@ExceptionHandler(PlaceException.class)
	public ResponseEntity<ErrorResponse> handlePlaceException(PlaceException ex) {

		log.error("PlaceError: {}", ex.getMessage());

		final ErrorResponse response = ErrorResponse.of(
			ex.getPlaceErrorCode().getErrorCode(),
			ex.getMessage()
		);
		return new ResponseEntity<>(response, ex.getPlaceErrorCode().getHttpStatus());
	}
}
