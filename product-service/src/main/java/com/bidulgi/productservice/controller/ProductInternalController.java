package com.bidulgi.productservice.controller;

import com.bidulgi.productservice.dto.request.UpdateStockRequest;
import com.bidulgi.productservice.service.ProductStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/products/stock")
@RequiredArgsConstructor
public class ProductInternalController {

    private final ProductStockService productStockService;

    // 1. 재고 차감 요청 (예약 서비스 -> 상품 서비스)
    @PostMapping("/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody @Valid UpdateStockRequest request) {
        productStockService.decreaseStock(request.getSlotId(), request.getCount());
        return ResponseEntity.ok().build();
    }

    // 2. 재고 복구 요청 (Rollback: 예약 취소/결제 실패 등)
    @PostMapping("/increase")
    public ResponseEntity<Void> increaseStock(@RequestBody @Valid UpdateStockRequest request) {
        productStockService.increaseStock(request.getSlotId(), request.getCount());
        return ResponseEntity.ok().build();
    }

}
