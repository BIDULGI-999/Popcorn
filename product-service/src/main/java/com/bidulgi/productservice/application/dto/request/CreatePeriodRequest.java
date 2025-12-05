package com.bidulgi.productservice.application.dto.request;

import com.bidulgi.productservice.domain.entity.Product;
import com.bidulgi.productservice.domain.entity.ProductPeriod;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreatePeriodRequest {

    @NotBlank private String name;
    @NotNull @FutureOrPresent private LocalDate periodStart;
    @NotNull @FutureOrPresent private LocalDate periodEnd;
    @NotNull private LocalDateTime saleStartAt;
    @NotNull private LocalDateTime saleEndAt;

    public ProductPeriod toEntity(Product product) {
        return ProductPeriod.builder()
                .product(product)
                .name(name)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .saleStartAt(saleStartAt)
                .saleEndAt(saleEndAt)
                .build();
    }


}
