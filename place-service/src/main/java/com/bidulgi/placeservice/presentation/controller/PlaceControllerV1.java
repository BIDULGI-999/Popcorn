package com.bidulgi.placeservice.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.placeservice.application.dto.PlaceResponse;
import com.bidulgi.placeservice.application.dto.PlaceSimpleResponse;
import com.bidulgi.placeservice.application.service.PlaceService;
import com.bidulgi.placeservice.presentation.request.CreatePlaceRequest;
import com.bidulgi.placeservice.presentation.request.UpdatePlaceRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/places")
public class PlaceControllerV1 {

	private final PlaceService placeService;

	@PostMapping
	public ApiResponse<PlaceResponse> createPlace(
		@AuthenticationPrincipal UserPrincipal user,
		@Valid @RequestBody CreatePlaceRequest request) {

		PlaceResponse response = placeService.createPlace(request.toCommand());
		return ApiResponse.success(response, "장소가 생성되었습니다.");
	}

	@PutMapping("/{placeId}")
	public ApiResponse<PlaceResponse> updatePlace(
		@AuthenticationPrincipal UserPrincipal user,
		@PathVariable UUID placeId,
		@Valid @RequestBody UpdatePlaceRequest request) {

		PlaceResponse response = placeService.updatePlace(request.toCommand(placeId));
		return ApiResponse.success(response, "장소가 수정되었습니다.");
	}

	@DeleteMapping("/{placeId}")
	public ApiResponse<Void> deletePlace(
		@AuthenticationPrincipal UserPrincipal user,
		@PathVariable UUID placeId) {

		placeService.deletePlace(placeId, user.id());
		return ApiResponse.success("장소가 삭제되었습니다.");
	}

	@GetMapping
	public ApiResponse<Page<PlaceSimpleResponse>> getPlaces(
		@RequestParam(required = false) String areaName,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

		Page<PlaceSimpleResponse> response = placeService.getPlaces(areaName, pageable);
		return ApiResponse.success(response);
	}

	@GetMapping("/{placeId}")
	public ApiResponse<PlaceResponse> getPlace(@PathVariable UUID placeId) {

		PlaceResponse response = placeService.getPlace(placeId);
		return ApiResponse.success(response);
	}
}
