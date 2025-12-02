package com.bidulgi.productservice.dto.request;

import com.bidulgi.productservice.entity.constant.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    @NotBlank private String name;
    @NotNull private Long price;
    private String description;
    private ProductStatus status;
}
