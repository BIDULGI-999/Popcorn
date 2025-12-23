package com.bidulgi.productservice.application.service;

import com.bidulgi.productservice.application.dto.response.ProductResponse;
import com.bidulgi.productservice.application.service.ProductRecommendationService;
import com.bidulgi.productservice.domain.entity.Product;
import com.bidulgi.productservice.domain.recommendation.AgeBand;
import com.bidulgi.productservice.infrastructure.client.UserClient;
import com.bidulgi.productservice.infrastructure.client.dto.UserProfileResponse;
import com.bidulgi.productservice.infrastructure.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductRecommendationServiceImpl implements ProductRecommendationService {

	private final ProductRepository productRepository;
	private final UserClient userClient;

	@Override
	public List<ProductResponse> recommendForUser(UUID userId) {


		UserProfileResponse profile = userClient.getUserProfile(userId);

		String gender = profile.gender() == null ? "UNKNOWN" : profile.gender().name();
		String ageBand = AgeBand.fromAge(profile.age()).name();

		List<Product> products = productRepository.findDemographicPopularOnSale(
			LocalDateTime.now(), gender, ageBand, 20
		);
		return products.stream().map(ProductResponse::from).toList();
	}

	@Override
	public List<ProductResponse> recommendNearbyForUser(UUID userId, UUID baseProductId) {

		UserProfileResponse profile = userClient.getUserProfile(userId);

		Product base = productRepository.findById(baseProductId)
			.orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

		String baseAddress = base.getAddress();

		if (baseAddress == null || baseAddress.isBlank()) {
			List<Product> popular = productRepository.findPopularOnSaleProducts(
				LocalDateTime.now(),
				PageRequest.of(0, 20)
			);
			return popular.stream()
				.map(ProductResponse::from)
				.toList();
		}

		List<Product> nearby = productRepository.findNearbyByAddress(
			baseProductId,
			baseAddress,
			LocalDateTime.now(),
			PageRequest.of(0, 20)
		);

		return nearby.stream()
			.map(ProductResponse::from)
			.toList();
	}
}
