package com.bidulgi.productservice.application.service;

import com.bidulgi.productservice.application.dto.response.ProductResponse;
import com.bidulgi.productservice.application.service.ProductRecommendationService;
import com.bidulgi.productservice.domain.entity.Product;
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

		// 1. 유저 프로필 조회
		UserProfileResponse profile = userClient.getUserProfile(userId);

		// 2. 나이/성별 기반 필터링 로직 (초기 버전: 일단 인기 상품을 베이스로)
		//    나중에 Product에 targetGender, minAge, maxAge 같은 필드 추가해서
		//    실제 where 조건/가중치에 반영하면 됨.

		List<Product> popular = productRepository.findPopularOnSaleProducts(
			LocalDateTime.now(),
			PageRequest.of(0, 20)
		);

		// TODO: profile.gender(), profile.age() 를 이용해 스코어 계산/필터링 로직 추가
		// ex) 여성 비율이 높은 카테고리 우선, 20대 선호 카테고리 우선 등

		return popular.stream()
			.map(ProductResponse::from)
			.toList();
	}

	@Override
	public List<ProductResponse> recommendNearbyForUser(UUID userId, UUID baseProductId) {

		// 유저 프로필을 여기서도 활용할 수 있음 (예: 같은 성별/나이대가 많이 방문한 팝업 우선 등)
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

		// TODO: profile 기반으로 근처 중에서도 우선 순위 조정 가능

		return nearby.stream()
			.map(ProductResponse::from)
			.toList();
	}
}
