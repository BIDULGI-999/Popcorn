package com.bidulgi.placeservice.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.placeservice.application.dto.PlaceSimpleResponse;
import com.bidulgi.placeservice.application.service.PlaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/places")
public class InternalPlaceController {

	private final PlaceService placeService;

	@GetMapping("/{placeId}")
	public ApiResponse<PlaceSimpleResponse> getPlaceInternal(@PathVariable UUID placeId) {

		PlaceSimpleResponse response = placeService.getPlaceInternal(placeId);
		return ApiResponse.success(response);
	}

	@GetMapping
	public ApiResponse<List<PlaceSimpleResponse>> getPlacesBulk(@RequestParam List<UUID> placeIds) {

		List<PlaceSimpleResponse> response = placeService.getPlacesBulk(placeIds);
		return ApiResponse.success(response);
	}
}
