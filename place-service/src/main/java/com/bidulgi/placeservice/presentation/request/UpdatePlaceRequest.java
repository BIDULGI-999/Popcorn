package com.bidulgi.placeservice.presentation.request;

import java.util.List;
import java.util.UUID;

import com.bidulgi.placeservice.application.dto.UpdatePlaceCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePlaceRequest(
	@NotBlank(message = "주소는 필수입니다.")
	String address,

	@NotNull(message = "위도는 필수입니다.")
	Double latitude,

	@NotNull(message = "경도는 필수입니다.")
	Double longitude,

	List<String> areaNames
) {
	public UpdatePlaceCommand toCommand(UUID placeId) {
		return new UpdatePlaceCommand(placeId, address, latitude, longitude, areaNames);
	}
}
