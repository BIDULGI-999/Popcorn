package com.bidulgi.productservice.presentation.controller;

import com.bidulgi.productservice.application.dto.request.UpdateStockRequest;
import com.bidulgi.productservice.application.facade.StockRedissonFacade;
import com.bidulgi.productservice.application.service.ProductStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final StockRedissonFacade stockRedissonFacade;

//    // 1. 재고 차감 요청 (예약 서비스 -> 상품 서비스)
//    @Operation(summary = "재고 차감", description = "예약 완료 시 슬롯 재고를 차감합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "재고 차감 성공"),
//            @ApiResponse(responseCode = "400", description = "재고 부족",
//                    content = @Content(schema = @Schema(implementation = String.class),
//                            examples = @ExampleObject(value = "{\"error\":\"재고가 부족합니다.\"}")))
//    })
//    @PostMapping("/decrease")
//    public ResponseEntity<Void> decreaseStock(@RequestBody @Valid UpdateStockRequest request) {
//        productStockService.decreaseStock(request.getSlotId(), request.getCount());
//        return ResponseEntity.ok().build();
//    }
//
//    // 2. 재고 복구 요청 (Rollback: 예약 취소/결제 실패 등)
//    @Operation(summary = "재고 복구", description = "예약 취소나 결제 실패 시 슬롯 재고를 복구합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "재고 복구 성공")
//    })
//    @PostMapping("/increase")
//    public ResponseEntity<Void> increaseStock(@RequestBody @Valid UpdateStockRequest request) {
//        productStockService.increaseStock(request.getSlotId(), request.getCount());
//        return ResponseEntity.ok().build();
//    }


    @PostMapping("/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody @Valid UpdateStockRequest request) {
        // Facade가 락을 잡고 -> Service를 호출해 줍니다.
        stockRedissonFacade.decreaseStock(request.getSlotId(), request.getCount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/increase")
    public ResponseEntity<Void> increaseStock(@RequestBody @Valid UpdateStockRequest request) {
        stockRedissonFacade.increaseStock(request.getSlotId(), request.getCount());
        return ResponseEntity.ok().build();
    }

}
