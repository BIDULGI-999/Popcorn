package com.bidulgi.productservice.presentation.controller;

import com.bidulgi.productservice.application.dto.request.CreatePeriodRequest;
import com.bidulgi.productservice.application.dto.request.CreateProductRequest;
import com.bidulgi.productservice.application.dto.request.UpdateProductRequest;
import com.bidulgi.productservice.application.dto.request.GenerateSlotRequest;
import com.bidulgi.productservice.application.dto.response.PeriodResponse;
import com.bidulgi.productservice.application.dto.response.ProductResponse;
import com.bidulgi.productservice.application.service.ProductPeriodService;
import com.bidulgi.productservice.application.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProductAdminController {

    private final ProductService productService;
    private final ProductPeriodService productPeriodService;

    // 1. 상품 등록
    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 등록 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    // 2. 상품 수정
    @Operation(summary = "상품 수정", description = "기존 상품 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 수정 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    })
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID productId,
            @RequestBody @Valid UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    // 3. 판매 회차(Period) 등록
    @Operation(summary = "회차 등록", description = "상품별 판매 회차(기간)를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 생성",
                        content = @Content(schema = @Schema(implementation = PeriodResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "검증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"error\":\"회차 기간은 행사 시작일 이후, 마감일 이전이어야 합니다.\"}"
                            )
                    )
            )
    })
    @PostMapping("/{productId}/periods")
    public ResponseEntity<PeriodResponse> createPeriod(
            @PathVariable UUID productId,
            @RequestBody @Valid CreatePeriodRequest request) {
        return ResponseEntity.ok(productPeriodService.createPeriod(productId, request));
    }

    // 4. 예약 슬롯 대량 생성 (Batch Create)
    // 규칙(시작/종료일, 시간 간격 등)을 받아 수백 개의 슬롯을 한번에 생성
    @Operation(summary = "예약 슬롯 생성", description = "회차 내 예약 슬롯을 규칙에 맞춰 대량 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "슬롯 생성 완료",
                    content = @Content(
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(
                                    value = "\"10개의 슬롯이 생성되었습니다.\""
                            )
                    ))
    })
    @PostMapping("/{productId}/periods/{periodId}/slots/generate")
    public ResponseEntity<String> generateSlots(
            @PathVariable UUID productId,
            @PathVariable UUID periodId,
            @RequestBody @Valid GenerateSlotRequest request) {

        int createdCount = productPeriodService.generateSlots(periodId, request);
        return ResponseEntity.ok(createdCount + "개의 슬롯이 생성되었습니다.");
    }
}
