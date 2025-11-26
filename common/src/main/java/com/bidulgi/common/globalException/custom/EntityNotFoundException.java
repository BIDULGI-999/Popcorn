package com.bidulgi.common.globalException.custom;

import com.bidulgi.common.globalException.ErrorCode;

public class EntityNotFoundException extends ServiceException {
	public EntityNotFoundException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public EntityNotFoundException(String message) {
		super(ErrorCode.RESOURCE_NOT_FOUND, message);
	}

	public EntityNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
