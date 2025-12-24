package com.bidulgi.productservice.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bidulgi.productservice.domain.recommendation.AgeBand;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	name = "p_product_demo_stats",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uq_product_gender_age",
			columnNames = {"product_id", "gender", "age_band"}
		)
	},
	indexes = {
		@Index(name = "idx_demo_gender_age", columnList = "gender, age_band"),
		@Index(name = "idx_demo_product", columnList = "product_id")
	}
)
public class ProductDemoStats {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
	private UUID id;

	@Column(name = "product_id", nullable = false)
	private UUID productId;

	@Column(name = "gender", nullable = false, length = 20)
	private String gender;

	@Enumerated(EnumType.STRING)
	@Column(name = "age_band", nullable = false, length = 20)
	private AgeBand ageBand;

	@Column(name = "favorite_count", nullable = false)
	private long favoriteCount;

	@Column(name = "like_count", nullable = false)
	private long likeCount;

	@Column(name = "view_count", nullable = false)
	private long viewCount;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public static ProductDemoStats create(UUID productId, String gender, AgeBand ageBand) {
		ProductDemoStats s = new ProductDemoStats();
		s.productId = productId;
		s.gender = gender;
		s.ageBand = ageBand;
		s.favoriteCount = 0L;
		s.likeCount = 0L;
		s.viewCount = 0L;
		s.updatedAt = LocalDateTime.now();
		return s;
	}

	public void incFavorite(long delta) {
		this.favoriteCount = Math.max(0L, this.favoriteCount + delta);
		this.updatedAt = LocalDateTime.now();
	}
}
