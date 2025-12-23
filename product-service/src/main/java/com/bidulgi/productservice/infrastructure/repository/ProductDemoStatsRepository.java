package com.bidulgi.productservice.infrastructure.repository;

import com.bidulgi.productservice.domain.entity.ProductDemoStats;
import com.bidulgi.productservice.domain.recommendation.AgeBand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductDemoStatsRepository extends JpaRepository<ProductDemoStats, UUID> {
	Optional<ProductDemoStats> findByProductIdAndGenderAndAgeBand(UUID productId, String gender, AgeBand ageBand);
}
