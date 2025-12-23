package com.bidulgi.productservice.presentation.exception;

import com.bidulgi.productservice.presentation.controller.ProductAdminController;
import com.bidulgi.productservice.presentation.exception.dto.response.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(
        annotations = {RestController.class},
        basePackageClasses = {
                ProductAdminController.class
        }
)
public class GlobalExceptionHandler {

    @ExceptionHandler(PeriodValidationException.class)
    public ResponseEntity<ErrorResponse> handlePeriodValidation(PeriodValidationException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("INVALID_PERIOD", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("DUPLICATE_SLOT", "중복된 슬롯이 존재합니다."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_ARGUMENT", ex.getMessage()));
    }



}
