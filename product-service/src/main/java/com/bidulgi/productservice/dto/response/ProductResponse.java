package com.bidulgi.productservice.dto.response;

import com.bidulgi.productservice.entity.Product;
import com.bidulgi.productservice.entity.constant.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ProductResponse {
    private UUID id;
    private UUID placeId;
    private String name;
    private Long price;
    private String description;
    private ProductStatus status;
    private String address;
    private Long likeCount;


    // Entity -> DTO 변환 정적 메서드 (Factory Method Pattern)
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .placeId(product.getPlaceId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .status(product.getStatus())
                .address(product.getAddress())
                .likeCount(product.getLikeCount())
                .build();
    }
}
