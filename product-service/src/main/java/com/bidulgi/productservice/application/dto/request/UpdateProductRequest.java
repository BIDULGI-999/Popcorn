package com.bidulgi.productservice.application.dto.request;

import com.bidulgi.productservice.domain.entity.constant.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateProductRequest {

    @NotBlank private String name;
    @NotNull private Long price;
    private String description;
    private ProductStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
