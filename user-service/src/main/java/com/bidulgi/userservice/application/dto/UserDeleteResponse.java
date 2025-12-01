package com.bidulgi.userservice.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDeleteResponse(
	UUID id,
	LocalDateTime deletedAT,
	UUID deletedBy
) {
}
