package com.bidulgi.productservice.repository;

import com.bidulgi.productservice.entity.ProductPeriod;
import com.bidulgi.productservice.entity.constant.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductPeriodRepository extends JpaRepository<ProductPeriod, UUID> {
    // 특정 상품의 모든 회차 조회
    List<ProductPeriod> findAllByProduct_Id(UUID productId);

    // 현재 판매 중인 회차만 조회
    List<ProductPeriod> findAllByProduct_IdAndStatus(UUID productId, ProductStatus status);
}
