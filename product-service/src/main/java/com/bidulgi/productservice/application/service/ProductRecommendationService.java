package com.bidulgi.productservice.application.service;

import com.bidulgi.productservice.application.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductRecommendationService {

	/**
	 * 유저의 성별/나이/선호(좋아요/찜) 등을 고려한 "나를 위한 추천"
	 * 지금 1차 버전은 일단 전체 인기 상품 기준
	 */
	List<ProductResponse> recommendForUser(UUID userId);

	/**
	 * 특정 팝업(productId) 방문 후,
	 * 같은 장소/근처 팝업 추천
	 */
	List<ProductResponse> recommendNearbyForUser(UUID userId, UUID baseProductId);
}
