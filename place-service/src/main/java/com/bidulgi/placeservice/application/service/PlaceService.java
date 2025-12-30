package com.bidulgi.placeservice.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidulgi.placeservice.application.dto.CreatePlaceCommand;
import com.bidulgi.placeservice.application.dto.PlaceResponse;
import com.bidulgi.placeservice.application.dto.PlaceSimpleResponse;
import com.bidulgi.placeservice.application.dto.UpdatePlaceCommand;
import com.bidulgi.placeservice.domain.exception.PlaceErrorCode;
import com.bidulgi.placeservice.domain.exception.PlaceException;
import com.bidulgi.placeservice.domain.model.Place;
import com.bidulgi.placeservice.domain.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

	private final PlaceRepository placeRepository;

	@Transactional
	public PlaceResponse createPlace(CreatePlaceCommand command) {
		Place place = Place.create(
			command.address(),
			command.latitude(),
			command.longitude()
		);

		if (command.areaNames() != null && !command.areaNames().isEmpty()) {
			command.areaNames().forEach(place::addArea);
		}

		Place savedPlace = placeRepository.save(place);
		return PlaceResponse.from(savedPlace);
	}

	@Transactional
	public PlaceResponse updatePlace(UpdatePlaceCommand command) {
		Place place = placeRepository.findById(command.placeId())
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.PLACE_NOT_FOUND));

		place.update(command.address(), command.latitude(), command.longitude());

		if (command.areaNames() != null) {
			place.updateAreas(command.areaNames());
		}

		return PlaceResponse.from(place);
	}

	@Transactional
	public void deletePlace(UUID placeId, UUID deletedBy) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.PLACE_NOT_FOUND));

		place.markAsDeleted(deletedBy);
	}

	@Transactional(readOnly = true)
	public PlaceResponse getPlace(UUID placeId) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.PLACE_NOT_FOUND));

		return PlaceResponse.from(place);
	}

	@Transactional(readOnly = true)
	public Page<PlaceSimpleResponse> getPlaces(String areaName, Pageable pageable) {
		Page<Place> places;

		if (areaName != null && !areaName.isBlank()) {
			places = placeRepository.findByAreaName(areaName, pageable);
		} else {
			places = placeRepository.findAll(pageable);
		}

		return places.map(PlaceSimpleResponse::from);
	}

	@Transactional(readOnly = true)
	public PlaceSimpleResponse getPlaceInternal(UUID placeId) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.PLACE_NOT_FOUND));

		return PlaceSimpleResponse.from(place);
	}

	@Transactional(readOnly = true)
	public List<PlaceSimpleResponse> getPlacesBulk(List<UUID> placeIds) {
		List<Place> places = placeRepository.findAllByIdIn(placeIds);

		return places.stream()
			.map(PlaceSimpleResponse::from)
			.toList();
	}
}
