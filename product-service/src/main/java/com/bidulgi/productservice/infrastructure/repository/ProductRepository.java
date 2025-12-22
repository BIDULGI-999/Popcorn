package com.bidulgi.productservice.infrastructure.repository;

import com.bidulgi.productservice.domain.entity.Product;
import com.bidulgi.productservice.domain.entity.constant.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    // 상태별 상품 조회
    List<Product> findAllByStatus(ProductStatus status);

    // 키워드 검색 (상품명 또는 설명에 키워드가 포함된 경우)
    Page<Product> findByNameContainingOrDescriptionContaining(String nameKeyword, String descKeyword, Pageable pageable);

    // 키워드가 없을 때 전체 조회 (페이징)
    Page<Product> findAll(Pageable pageable);

	/**
	 * 앞으로 진행 중/예정인 상품 중,
	 * 좋아요 + 찜 + 조회수 합 기준으로 인기 상품 N개
	 */
	@Query("""
        SELECT p
        FROM Product p
        WHERE p.status = com.bidulgi.productservice.domain.entity.constant.ProductStatus.ON_SALE
          AND (p.startDate IS NULL OR p.startDate <= :now)
          AND (p.endDate IS NULL OR p.endDate >= :now)
        ORDER BY (COALESCE(p.likeCount, 0) 
               + COALESCE(p.favoriteCount, 0) 
               + COALESCE(p.viewCount, 0)) DESC
        """)
	List<Product> findPopularOnSaleProducts(@Param("now") LocalDateTime now, Pageable pageable);

	/**
	 * 기준 상품과 같은 주소(= 같은 장소) 기반으로
	 * 근처 상품 추천 (상태/기간 필터 포함)
	 *
	 * TODO: 실제로는 place 서비스에서 위/경도 정보를 받아와서
	 *      거리 기반으로 정렬하는 게 더 좋음
	 */
	@Query("""
        SELECT p
        FROM Product p
        WHERE p.id <> :baseProductId
          AND p.status = com.bidulgi.productservice.domain.entity.constant.ProductStatus.ON_SALE
          AND (p.startDate IS NULL OR p.startDate <= :now)
          AND (p.endDate IS NULL OR p.endDate >= :now)
          AND p.address = :address
        ORDER BY (COALESCE(p.likeCount, 0) 
               + COALESCE(p.favoriteCount, 0) 
               + COALESCE(p.viewCount, 0)) DESC
        """)
	List<Product> findNearbyByAddress(@Param("baseProductId") UUID baseProductId,
		@Param("address") String address,
		@Param("now") LocalDateTime now,
		Pageable pageable);
}
