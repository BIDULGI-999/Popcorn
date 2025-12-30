package com.bidulgi.placeservice.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bidulgi.placeservice.domain.model.Place;
import com.bidulgi.placeservice.domain.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryAdaptor implements PlaceRepository {

	private final JpaPlaceRepository jpaPlaceRepository;

	@Override
	public Place save(Place place) {
		return jpaPlaceRepository.save(place);
	}

	@Override
	public Optional<Place> findById(UUID id) {
		return jpaPlaceRepository.findById(id);
	}

	@Override
	public List<Place> findAllByIdIn(List<UUID> ids) {
		return jpaPlaceRepository.findAllByIdIn(ids);
	}

	@Override
	public Page<Place> findAll(Pageable pageable) {
		return jpaPlaceRepository.findAll(pageable);
	}

	@Override
	public Page<Place> findByAreaName(String areaName, Pageable pageable) {
		return jpaPlaceRepository.findByAreaName(areaName, pageable);
	}

	@Override
	public void delete(Place place) {
		jpaPlaceRepository.delete(place);
	}
}
