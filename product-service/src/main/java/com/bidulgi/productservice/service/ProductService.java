package com.bidulgi.productservice.service;

import com.bidulgi.productservice.dto.request.CreateProductRequest;
import com.bidulgi.productservice.dto.request.UpdateProductRequest;
import com.bidulgi.productservice.dto.response.ProductResponse;
import com.bidulgi.productservice.entity.Product;
import com.bidulgi.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 등록
     */
    @Transactional // 쓰기 트랜잭션 필요
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = request.toEntity();
        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }

    /**
     * 상품 수정
     * Dirty Checking(변경 감지)을 이용해 save() 호출 없이 트랜잭션 종료 시 update 쿼리 발생
     */
    @Transactional
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
        Product product = findByIdOrThrow(productId);

        // 엔티티의 비즈니스 메서드를 통해 데이터 수정
        product.update(
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getStatus(),
                null // 주소 수정 로직은 필요 시 추가 (DTO에 필드 추가 필요)
        );

        return ProductResponse.from(product);
    }

    /**
     * 상품 목록 조회 (페이징 + 검색)
     */
    public Page<ProductResponse> getProducts(Pageable pageable, String keyword) {
        Page<Product> productPage;

        if (keyword != null && !keyword.isBlank()) {
            // 키워드가 있으면 검색
            productPage = productRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);
        } else {
            // 없으면 전체 조회
            productPage = productRepository.findAll(pageable);
        }

        // Entity Page -> DTO Page 변환
        return productPage.map(ProductResponse::from);
    }

    /**
     * 상품 상세 조회
     */
    public ProductResponse getProduct(UUID productId) {
        Product product = findByIdOrThrow(productId);
        return ProductResponse.from(product);
    }

    // --- 내부 편의 메서드 ---
    private Product findByIdOrThrow(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. ID=" + productId));
    }

}
