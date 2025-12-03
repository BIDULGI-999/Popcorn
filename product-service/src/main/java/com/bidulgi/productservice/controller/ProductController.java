package com.bidulgi.productservice.controller;

import brave.Response;
import com.bidulgi.productservice.dto.response.ProductResponse;
import com.bidulgi.productservice.dto.response.SlotResponse;
import com.bidulgi.productservice.service.ProductInteractionService;
import com.bidulgi.productservice.service.ProductPeriodService;
import com.bidulgi.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductPeriodService productPeriodService;
    private final ProductInteractionService productInteractionService;

    // 1. 상품 목록 조회 (페이징 필터)
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseEntity.ok(productService.getProducts(pageable, keyword));
    }

    // 2. 상품 상세 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductDetail(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    // 3. 예약 가능 슬롯(재고) 조회
    @GetMapping("/{productId}/periods/{periodId}/slots")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @PathVariable UUID productId,
            @PathVariable UUID periodId,
            @RequestParam LocalDate date
    ) {
        return ResponseEntity.ok(productPeriodService.getSlotsByDate(periodId, date));
    }

    // 4. 상품 좋아요 토글
    @PostMapping("/{productId}/likes")
    public ResponseEntity<Void> toggleLike(@PathVariable UUID productId) {
        // TODO: 실제 구현 시 SecurityContext에서 userId를 가져와야 함
        UUID userId = UUID.randomUUID();
        productInteractionService.toggleLike(productId, userId);
        return ResponseEntity.ok().build();
    }

    // 5. 상품 찜하기 토글
    @PostMapping("/{productId}/favorites")
    public ResponseEntity<Void> toggleFavorite(@PathVariable UUID productId) {
        // TODO: 실제 구현 시 SecurityContext에서 userId를 가져와야 함
        UUID userId = UUID.randomUUID();
        productInteractionService.toggleFavorite(productId, userId);
        return ResponseEntity.ok().build();
    }

    // 6. 내가 찜한 목록 조회
    @GetMapping("/favorites")
    public ResponseEntity<List<ProductResponse>> getMyFavorites() {
        UUID userId = UUID.randomUUID();
        return ResponseEntity.ok(productInteractionService.getMyFavorites(userId));
    }
}
