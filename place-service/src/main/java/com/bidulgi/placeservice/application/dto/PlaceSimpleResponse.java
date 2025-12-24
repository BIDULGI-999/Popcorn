package com.bidulgi.placeservice.application.dto;

import java.util.UUID;

import com.bidulgi.placeservice.domain.model.Place;

public record PlaceSimpleResponse(
	UUID id,
	String address,
	Double latitude,
	Double longitude
) {
	public static PlaceSimpleResponse from(Place place) {
		return new PlaceSimpleResponse(
			place.getId(),
			place.getAddress(),
			place.getLatitude(),
			place.getLongitude()
		);
	}
}
