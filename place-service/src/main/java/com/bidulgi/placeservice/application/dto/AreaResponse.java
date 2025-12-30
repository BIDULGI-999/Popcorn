package com.bidulgi.placeservice.application.dto;

import com.bidulgi.placeservice.domain.model.Area;

public record AreaResponse(
	Long id,
	String name
) {
	public static AreaResponse from(Area area) {
		return new AreaResponse(area.getId(), area.getName());
	}
}
