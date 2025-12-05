package com.bidulgi.productservice.application.dto.request;

import com.bidulgi.productservice.domain.entity.Product;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateProductRequest {

    @NotNull(message = "장소 ID는 필수입니다.")
    private UUID placeId;

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    private Long price;

    @NotBlank(message = "상품 설명은 필수입니다.")
    private String description;

    @NotNull(message = "행사 시작일은 필수입니다.")
    private LocalDateTime startDate;

    @NotNull(message = "행사 종료일은 필수입니다.")
    @FutureOrPresent
    private LocalDateTime endDate;

    private String address; // 장소 주소 (캐싱용)

    // DTO -> Entity 변환 메서드
    public Product toEntity() {
        return Product.builder()
                .placeId(placeId)
                .name(name)
                .price(price)
                .description(description)
                .address(address)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
