package com.bidulgi.placeservice.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.bidulgi.common.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_place")
public class Place extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Area> areas = new ArrayList<>();

	@Builder
	private Place(String address, Double latitude, Double longitude) {
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public static Place create(String address, Double latitude, Double longitude) {
		return Place.builder()
			.address(address)
			.latitude(latitude)
			.longitude(longitude)
			.build();
	}

	public void update(String address, Double latitude, Double longitude) {
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public void addArea(String areaName) {
		Area area = new Area(this, areaName);
		this.areas.add(area);
	}

	public void clearAreas() {
		this.areas.clear();
	}

	public void updateAreas(List<String> areaNames) {
		this.areas.clear();
		for (String name : areaNames) {
			addArea(name);
		}
	}

	public List<Area> getAreas() {
		return Collections.unmodifiableList(areas);
	}
}
