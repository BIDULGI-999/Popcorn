package com.bidulgi.productservice.application.service;

import com.bidulgi.productservice.application.dto.response.ProductResponse;
import com.bidulgi.productservice.domain.entity.Product;
import com.bidulgi.productservice.domain.entity.ProductFavorite;
import com.bidulgi.productservice.infrastructure.client.UserClient;
import com.bidulgi.productservice.infrastructure.repository.ProductDemoStatsRepository;
import com.bidulgi.productservice.infrastructure.repository.ProductFavoriteRepository;
import com.bidulgi.productservice.infrastructure.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInteractionService {

    private final ProductRepository productRepository;
    private final ProductFavoriteRepository productFavoriteRepository;
    private final RedisTemplate<String, Object> redisTemplate;

	private final UserClient userClient;
	private final ProductDemoStatsRepository productDemoStatsRepository;

    // --- Redis Key 패턴 정의 ---
    // 좋아요 저장 (Set): product:like:{productId} -> {userId1, userId2...}
    // Set을 사용하여 한 유저가 여러 번 눌러도 중복되지 않도록 관리합니다.
    private static final String KEY_LIKE_SET = "product:like:%s";

    // 조회수 저장 (String/Value): product:view:{productId} -> "100"
    // 단순 카운트만 필요하므로 Value 연산을 사용합니다.
    private static final String KEY_VIEW_COUNT = "product:view:%s";

    // 배치 작업을 위한 '변경된 상품 ID 목록' (Set)
    // 스케쥴러는 이 Set에 들어있는 ID만 DB와 동기화하여 부하를 줄입니다.
    private static final String KEY_DIRTY_LIKES = "product:dirty:likes";
    private static final String KEY_DIRTY_VIEWS = "product:dirty:views";

    /**
     * [Redis] 좋아요 토글
     * DB 부하를 줄이기 위해 Redis Set을 사용하며, 실제 DB 반영은 스케쥴러가 비동기로 수행합니다.
     * @param productId 상품 ID
     * @param userId 유저 ID
     */
    public void toggleLike(UUID productId, UUID userId) {
        String key = String.format(KEY_LIKE_SET, productId.toString());
		String member = userId.toString();

        // 1. 이미 좋아요를 눌렀는지 Redis Set에서 확인 (SISMEMBER)
        Boolean isMember = redisTemplate.opsForSet().isMember(key, member);

        if (Boolean.TRUE.equals(isMember)) {
            // 이미 존재하면 -> 좋아요 취소 (Set에서 제거)
            redisTemplate.opsForSet().remove(key, member);
        } else {
            // 없으면 -> 좋아요 추가 (Set에 추가)
            redisTemplate.opsForSet().add(key, member);
        }

        // 2. 변경 사항이 발생한 상품 ID를 Dirty Set에 기록 (배치 처리용)
        // 나중에 스케줄러가 이 ID를 보고 DB의 likeCount를 갱신합니다.
        redisTemplate.opsForSet().add(KEY_DIRTY_LIKES, productId.toString());
    }

    /**
     * [Redis] 조회수 증가
     * 단순 INCR 연산을 사용하여 매우 빠르게 처리합니다.
     *
     * @param productId 상품 ID
     */
    public void increaseViewCount(UUID productId) {
        String key = String.format(KEY_VIEW_COUNT, productId.toString());

        // 1. 조회수 1 증가 (INCR)
        redisTemplate.opsForValue().increment(key);

        // 2. 변경 사항이 발생한 상품 ID를 Dirty Set에 기록
        redisTemplate.opsForSet().add(KEY_DIRTY_VIEWS, productId.toString());
    }

    /**
     * [DB] 찜하기 토글 (즐겨찾기)
     * 개인화된 중요 데이터이므로 DB 트랜잭션을 사용하여 즉시 처리합니다.
     * Product 테이블의 favoriteCount(반정규화 컬럼)도 함께 갱신하여 목록 조회 성능을 높입니다.
     *
     * @param productId 상품 ID
     * @param userId 유저 ID
     */
	@Transactional
	public void toggleFavorite(UUID productId, UUID userId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID=" + productId));

		// 1) 유저 프로필 조회 (성별/나이대 얻기)
		var profile = userClient.getUserProfile(userId);

		String genderKey = (profile.gender() == null) ? "UNKNOWN" : profile.gender().name();
		var ageBand = com.bidulgi.productservice.domain.recommendation.AgeBand.fromAge(profile.age());

		// 2) 찜 여부 확인
		Optional<ProductFavorite> existing =
			productFavoriteRepository.findByUserIdAndProduct_Id(userId, productId);

		if (existing.isPresent()) {
			productFavoriteRepository.delete(existing.get());

			long current = product.getFavoriteCount() == null ? 0 : product.getFavoriteCount();
			product.updateFavoriteCount(Math.max(0, current - 1));

			applyFavoriteDemoStats(productId, genderKey, ageBand, -1);

		} else {
			ProductFavorite favorite = ProductFavorite.builder()
				.userId(userId)
				.product(product)
				.build();
			productFavoriteRepository.save(favorite);

			long current = product.getFavoriteCount() == null ? 0 : product.getFavoriteCount();
			product.updateFavoriteCount(current + 1);

			// demographic stats +1
			applyFavoriteDemoStats(productId, genderKey, ageBand, +1);
		}
	}

	private void applyFavoriteDemoStats(UUID productId, String gender, com.bidulgi.productservice.domain.recommendation.AgeBand ageBand, long delta) {
		var stats = productDemoStatsRepository
			.findByProductIdAndGenderAndAgeBand(productId, gender, ageBand)
			.orElseGet(() -> productDemoStatsRepository.save(
				com.bidulgi.productservice.domain.entity.ProductDemoStats.create(productId, gender, ageBand)
			));

		stats.incFavorite(delta);
	}

    /**
     * [DB] 내가 찜한 목록 조회
     * @param userId 유저 ID
     * @return 찜한 상품 목록 (DTO)
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getMyFavorites(UUID userId) {
        // userId 로 바꿔서 찜 내역 조회
        List<ProductFavorite> favorites = productFavoriteRepository.findAllByUserId(userId);

        // Entity -> DTO 변환하여 반환
        return favorites.stream()
                .map(ProductFavorite::getProduct) // ProductFavorite에서 Product 추출
                .map(ProductResponse::from)// Product를 DTO로 변환
                .collect(Collectors.toList());
    }

}
