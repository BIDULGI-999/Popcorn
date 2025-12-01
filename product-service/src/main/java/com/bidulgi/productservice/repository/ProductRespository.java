package com.bidulgi.productservice.repository;

import com.bidulgi.productservice.entity.Product;
import com.bidulgi.productservice.entity.constant.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRespository extends JpaRepository<Product, UUID> {
    // 상태별 상품 조회
    List<Product> findAllByStatus(ProductStatus status);
}
