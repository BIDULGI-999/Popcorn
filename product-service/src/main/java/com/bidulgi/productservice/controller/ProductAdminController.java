package com.bidulgi.productservice.controller;

import com.bidulgi.productservice.dto.request.CreatePeriodRequest;
import com.bidulgi.productservice.dto.request.CreateProductRequest;
import com.bidulgi.productservice.dto.request.UpdateProductRequest;
import com.bidulgi.productservice.dto.request.GenerateSlotRequest;
import com.bidulgi.productservice.dto.response.PeriodResponse;
import com.bidulgi.productservice.dto.response.ProductResponse;
import com.bidulgi.productservice.service.ProductPeriodService;
import com.bidulgi.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;
    private final ProductPeriodService productPeriodService;

    // 1. 상품 등록
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    // 2. 상품 수정
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID productId,
            @RequestBody @Valid UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    // 3. 판매 회차(Period) 등록
    @PostMapping("/{productId}/periods")
    public ResponseEntity<PeriodResponse> createPeriod(
            @PathVariable UUID productId,
            @RequestBody @Valid CreatePeriodRequest request) {
        return ResponseEntity.ok(productPeriodService.createPeriod(productId, request));
    }

    // 4. 예약 슬롯 대량 생성 (Batch Create)
    // 규칙(시작/종료일, 시간 간격 등)을 받아 수백 개의 슬롯을 한번에 생성
    @PostMapping("/{productId}/periods/{periodId}/slots/generate")
    public ResponseEntity<String> generateSlots(
            @PathVariable UUID productId,
            @PathVariable UUID periodId,
            @RequestBody @Valid GenerateSlotRequest request) {

        int createdCount = productPeriodService.generateSlots(periodId, request);
        return ResponseEntity.ok(createdCount + "개의 슬롯이 생성되었습니다.");
    }
}
