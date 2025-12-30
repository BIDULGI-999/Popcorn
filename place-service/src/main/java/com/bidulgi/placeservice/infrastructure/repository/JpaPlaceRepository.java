package com.bidulgi.placeservice.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bidulgi.placeservice.domain.model.Place;

public interface JpaPlaceRepository extends JpaRepository<Place, UUID> {

	List<Place> findAllByIdIn(List<UUID> ids);

	@Query("SELECT DISTINCT p FROM Place p JOIN p.areas a WHERE a.name = :areaName")
	Page<Place> findByAreaName(@Param("areaName") String areaName, Pageable pageable);
}
