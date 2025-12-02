package com.bidulgi.productservice.repository;

import com.bidulgi.productservice.entity.Product;
import com.bidulgi.productservice.entity.constant.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    // 상태별 상품 조회
    List<Product> findAllByStatus(ProductStatus status);

    // 키워드 검색 (상품명 또는 설명에 키워드가 포함된 경우)
    Page<Product> findByNameContainingOrDescriptionContaining(String nameKeyword, String descKeyword, Pageable pageable);

    // 키워드가 없을 때 전체 조회 (페이징)
    Page<Product> findAll(Pageable pageable);
}
