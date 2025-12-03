package com.bidulgi.productservice.infrastructure.repository;

import com.bidulgi.productservice.domain.entity.ProductFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Long> {

    // 이미 찜했는지 확인
    Optional<ProductFavorite> findByUserIdAndProduct_Id(UUID userId, UUID productId);

    // 내 찜 목록 조회
    List<ProductFavorite> findAllByUserId(UUID userId);
}
