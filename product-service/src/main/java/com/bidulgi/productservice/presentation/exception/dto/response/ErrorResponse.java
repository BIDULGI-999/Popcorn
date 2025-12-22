package com.bidulgi.productservice.presentation.exception.dto.response;

public record ErrorResponse (
    String error,
    String message
) {}
