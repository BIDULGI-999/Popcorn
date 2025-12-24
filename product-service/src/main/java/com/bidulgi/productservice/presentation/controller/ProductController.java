package com.bidulgi.productservice.presentation.controller;

import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.productservice.application.dto.response.ProductResponse;
import com.bidulgi.productservice.application.dto.response.SlotResponse;
import com.bidulgi.productservice.application.service.ProductInteractionService;
import com.bidulgi.productservice.application.service.ProductPeriodService;
import com.bidulgi.productservice.application.service.ProductRecommendationService;
import com.bidulgi.productservice.application.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductPeriodService productPeriodService;
    private final ProductInteractionService productInteractionService;
	private final ProductRecommendationService productRecommendationService;

    // 1. 상품 목록 조회 (페이징 필터)
    @Operation(summary = "상품 목록 조회", description = "페이징과 키워드 필터를 적용하여 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
	@GetMapping("/search")
	public ResponseEntity<Page<ProductResponse>> getProducts(
		@ParameterObject Pageable pageable,
		@RequestParam(required = false) String keyword
	) {
		Pageable safe = sanitizePageable(pageable);
		return ResponseEntity.ok(productService.getProducts(safe, keyword));
	}

	private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
		"createdAt", "name", "price" // Product/BaseEntity에 실제 존재 필드만
	);

	private Pageable sanitizePageable(Pageable pageable) {
		Sort safeSort = Sort.unsorted();

		for (Sort.Order order : pageable.getSort()) {
			String raw = order.getProperty();
			String prop = normalizeSortProperty(raw); // ✅ ["name"] -> name

			if (ALLOWED_SORT_PROPERTIES.contains(prop)) {
				safeSort = safeSort.and(Sort.by(new Sort.Order(order.getDirection(), prop)));
			}
		}

		if (safeSort.isUnsorted()) {
			safeSort = Sort.by(Sort.Order.desc("createdAt"));
		}

		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);
	}

	/** "name" / ["name"] / "\"name\"" 같은 입력을 name으로 정규화 */
	private String normalizeSortProperty(String raw) {
		if (raw == null) return "";
		String s = raw.trim();

		// ["name"] 형태 제거
		if (s.startsWith("[") && s.endsWith("]")) {
			s = s.substring(1, s.length() - 1).trim();
		}
		// "name" 형태 제거
		if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
			s = s.substring(1, s.length() - 1).trim();
		}
		return s;
	}

    // 2. 상품 상세 조회
    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "{\"error\":\"상품을 찾을 수 없습니다.\"}")))
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductDetail(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    // 3. 예약 가능 슬롯(재고) 조회
    @Operation(summary = "예약 가능 슬롯 조회", description = "특정 회차와 날짜에 예약 가능한 슬롯 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 슬롯 조회 성공",
                    content = @Content(schema = @Schema(implementation = SlotResponse.class)))
    })
    @GetMapping("/{productId}/periods/{periodId}/slots")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @PathVariable UUID productId,
            @PathVariable UUID periodId,
            @RequestParam LocalDate date
    ) {
        return ResponseEntity.ok(productPeriodService.getSlotsByDate(periodId, date));
    }

    // 4. 상품 좋아요 토글
    @Operation(summary = "상품 좋아요 토글", description = "사용자가 상품을 좋아요/좋아요 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 상태 변경 성공")
    })
    @PostMapping("/{productId}/likes")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<Void> toggleLike(@PathVariable UUID productId, @AuthenticationPrincipal UserPrincipal principal) {
        productInteractionService.toggleLike(productId, principal.id());
        return ResponseEntity.ok().build();
    }

    // 5. 상품 찜하기 토글
    @Operation(summary = "상품 찜하기 토글", description = "사용자가 상품을 찜/찜 해제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "찜 상태 변경 성공")
    })
    @PostMapping("/{productId}/favorites")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<Void> toggleFavorite(@PathVariable UUID productId, @AuthenticationPrincipal UserPrincipal principal) {
        productInteractionService.toggleFavorite(productId, principal.id());
        return ResponseEntity.ok().build();
    }

    // 6. 내가 찜한 목록 조회
    @Operation(summary = "내 찜 목록 조회", description = "사용자가 찜한 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "찜 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class)))
    })
    @GetMapping("/favorites")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<List<ProductResponse>> getMyFavorites(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(productInteractionService.getMyFavorites(principal.id()));
    }

	// 7. [추천] 나를 위한 추천
	@GetMapping("/recommendations/for-me")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<List<ProductResponse>> getRecommendationsForMe(
		@RequestHeader("X-User-Id") UUID userId
	) {
		return ResponseEntity.ok(productRecommendationService.recommendForUser(userId));
	}

	// 8. [추천] 특정 팝업 기준 근처 추천
	@GetMapping("/{productId}/recommendations/nearby")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<List<ProductResponse>> getNearbyRecommendations(
		@RequestHeader("X-User-Id") UUID userId,
		@PathVariable UUID productId
	) {
		return ResponseEntity.ok(productRecommendationService.recommendNearbyForUser(userId, productId));
	}
}
