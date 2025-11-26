package com.bidulgi.common.globalException.custom;

import com.bidulgi.common.globalException.ErrorCode;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

	private ErrorCode errorCode;

	public ServiceException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ServiceException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
