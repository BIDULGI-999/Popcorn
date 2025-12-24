package com.bidulgi.placeservice.application.dto;

import java.util.List;
import java.util.UUID;

public record UpdatePlaceCommand(
	UUID placeId,
	String address,
	Double latitude,
	Double longitude,
	List<String> areaNames
) {
}
