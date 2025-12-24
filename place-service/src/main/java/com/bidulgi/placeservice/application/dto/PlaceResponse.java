package com.bidulgi.placeservice.application.dto;

import java.util.List;
import java.util.UUID;

import com.bidulgi.placeservice.domain.model.Place;

public record PlaceResponse(
	UUID id,
	String address,
	Double latitude,
	Double longitude,
	List<AreaResponse> areas
) {
	public static PlaceResponse from(Place place) {
		List<AreaResponse> areaResponses = place.getAreas().stream()
			.map(AreaResponse::from)
			.toList();

		return new PlaceResponse(
			place.getId(),
			place.getAddress(),
			place.getLatitude(),
			place.getLongitude(),
			areaResponses
		);
	}
}
