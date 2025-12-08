package com.bidulgi.userservice.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeleteUserResponse(
	UUID id,
	LocalDateTime deletedAT,
	UUID deletedBy
) {
}
