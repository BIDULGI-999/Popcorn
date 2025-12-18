package com.bidulgi.queueservice.presentation;

import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import com.bidulgi.common.globalException.ErrorCode;
import com.bidulgi.common.globalException.ErrorResponse;
import com.bidulgi.common.globalException.custom.AuthorizationException;
import com.bidulgi.common.globalException.custom.EntityNotFoundException;
import com.bidulgi.common.globalException.custom.ExternalServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({
		IllegalArgumentException.class,
		NoSuchElementException.class,
		DateTimeParseException.class,
		ServerWebInputException.class
	})
	public ResponseEntity<ErrorResponse> handleCommonException(Exception e) {
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE, e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
		final ErrorResponse response = ErrorResponse.of(ErrorCode.RESOURCE_NOT_FOUND, e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(AuthorizationException.class)
	public ResponseEntity<ErrorResponse> handleAuthorizationException(AuthorizationException e) {
		final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	/**
	 * @Valid 으로 binding error 발생시
	 */
	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(WebExchangeBindException e) {
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * @Validated 으로 binding error 발생시
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * @ModelAttribute 으로 binding error 발생시
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		final ErrorResponse response = ErrorResponse.of(e);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
	 * 지원하지 않은 HTTP method로 호출 할 경우
	 */
	@ExceptionHandler(MethodNotAllowedException.class)
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(MethodNotAllowedException e) {
		final ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
		return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
		if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
			return new ResponseEntity<>(ErrorResponse.of(ErrorCode.RESOURCE_NOT_FOUND, e.getReason()), HttpStatus.NOT_FOUND);
		}
		final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_REQUEST, e.getReason());
		return new ResponseEntity<>(response, e.getStatusCode());
	}

	@ExceptionHandler(ExternalServiceException.class)
	public ResponseEntity<ErrorResponse> handleExternalServiceException(ExternalServiceException e) {
		log.error("handleExternalServiceException: {}", e.getMessage());
		final ErrorResponse response = ErrorResponse.of(e.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("{}: {}", e.getClass(), e.getMessage());
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
