package com.bidulgi.common.globalException.custom;

import com.bidulgi.common.globalException.ErrorCode;

public class AuthorizationException extends ServiceException {
	public AuthorizationException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public AuthorizationException(String message) {
		super(ErrorCode.FORBIDDEN_ACCESS, message);
	}

	public AuthorizationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
