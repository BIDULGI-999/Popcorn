package com.bidulgi.placeservice.domain.exception;

import lombok.Getter;

@Getter
public class PlaceException extends RuntimeException {

	private PlaceErrorCode placeErrorCode;

	public PlaceException(PlaceErrorCode placeErrorCode) {
		super(placeErrorCode.getMessage());
		this.placeErrorCode = placeErrorCode;
	}

	public PlaceException(PlaceErrorCode placeErrorCode, String detail) {
		super(placeErrorCode.getMessage() + " - " + detail);
		this.placeErrorCode = placeErrorCode;
	}

	public PlaceException(PlaceErrorCode placeErrorCode, Throwable cause) {
		super(placeErrorCode.getMessage(), cause);
		this.placeErrorCode = placeErrorCode;
	}
}
