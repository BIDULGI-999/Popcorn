package com.bidulgi.productservice.scheduler;

import com.bidulgi.productservice.entity.Product;
import com.bidulgi.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSyncScheduler {
    private final RedisTemplate<String, String> redisTemplate;
    private final ProductRepository productRepository;

    private static final String KEY_LIKE_SET = "product:like:%s";
    private static final String KEY_VIEW_COUNT = "product:view:%s";
    private static final String KEY_DIRTY_LIKES = "product:dirty:likes";
    private static final String KEY_DIRTY_VIEWS = "product:dirty:views";

    /**
     * [배치 1] 좋아요 수 동기화 (Redis -> DB)
     * 1분마다 실행 (예시)
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void syncLikeCounts() {
        // 1. 변경된 상품 목록(Dirty Set) 가져오기 및 제거 (Pop)
        // pop()을 지원하면 좋지만, 여기서는 members 조회 후
        Set<String> dirtyProductIds = redisTemplate.opsForSet().members(KEY_DIRTY_LIKES);

        if (dirtyProductIds == null || dirtyProductIds.isEmpty()) {
            return;
        }

        log.info("Starting Like Count Sync for {} products", dirtyProductIds.size());

        for (String prodIdStr : dirtyProductIds) {
            try {
                UUID productId = UUID.fromString(prodIdStr);
                String likeKey = String.format(KEY_LIKE_SET, prodIdStr);

                // 2. Redis에서 현재 좋아요 총 개수 조회 (SCARD)
                Long count = redisTemplate.opsForSet().size(likeKey);
                if (count == null) count = 0L;

                // 3. DB 업데이트
                // (더 효율적인 방법: updateQuery를 사용하여 count만 갱신)
                // productRepository.updateLikeCount(productId, count); -> JPQL 권장
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    // Entity에 updateLikeCount 메서드 필요 (Setter 대신 비즈니스 메서드 사용)
                     product.updateLikeCount(count);
                }

                // 4. 처리 완료 후 Dirty Set에서 해당 ID 제거
                redisTemplate.opsForSet().remove(KEY_DIRTY_LIKES, prodIdStr);

            } catch (Exception e) {
                log.error("Failed to sync like count for product {}", prodIdStr, e);
            }
        }
    }

    /**
     * [배치 2] 조회수 동기화
     * 5분마다 실행
     */
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void syncViewCounts() {
        Set<String> dirtyProductIds = redisTemplate.opsForSet().members(KEY_DIRTY_VIEWS);

        if (dirtyProductIds == null || dirtyProductIds.isEmpty()) {
            return;
        }

        log.info("Starting View Count Sync for {} products", dirtyProductIds.size());

        for (String prodIdStr : dirtyProductIds) {
            UUID productId = UUID.fromString(prodIdStr);
            String viewKey = String.format(KEY_VIEW_COUNT, prodIdStr);

            String viewCountStr = redisTemplate.opsForValue().get(viewKey);
            long viewCount = (viewCountStr != null) ? Long.parseLong(viewCountStr) : 0L;

            // DB 업데이트 (단순 덮어쓰기보다는, DB 값 + Redis 증분(diff) 방식이 더 안전할 수 있음)
            // 여기서는 단순 덮어쓰기 로직 예시
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                 product.updateViewCount(viewCount);
            }

            redisTemplate.opsForSet().remove(KEY_DIRTY_VIEWS, prodIdStr);
        }
    }
}
