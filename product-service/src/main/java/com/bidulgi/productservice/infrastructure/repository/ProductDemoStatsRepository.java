package com.bidulgi.productservice.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidulgi.productservice.domain.entity.ProductDemoStats;
import com.bidulgi.productservice.domain.recommendation.AgeBand;

public interface ProductDemoStatsRepository extends JpaRepository<ProductDemoStats, UUID> {
	Optional<ProductDemoStats> findByProductIdAndGenderAndAgeBand(UUID productId, String gender, AgeBand ageBand);
}
