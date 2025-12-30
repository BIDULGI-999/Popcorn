package com.bidulgi.placeservice.application.dto;

import java.util.List;

public record CreatePlaceCommand(
	String address,
	Double latitude,
	Double longitude,
	List<String> areaNames
) {
}
