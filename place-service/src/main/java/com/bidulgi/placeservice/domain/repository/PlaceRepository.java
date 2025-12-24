package com.bidulgi.placeservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bidulgi.placeservice.domain.model.Place;

public interface PlaceRepository {

	Place save(Place place);

	Optional<Place> findById(UUID id);

	List<Place> findAllByIdIn(List<UUID> ids);

	Page<Place> findAll(Pageable pageable);

	Page<Place> findByAreaName(String areaName, Pageable pageable);

	void delete(Place place);
}
