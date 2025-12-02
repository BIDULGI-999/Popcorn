package com.bidulgi.productservice.dto.response;

import com.bidulgi.productservice.entity.ProductPeriod;
import com.bidulgi.productservice.entity.constant.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class PeriodResponse {
    private UUID id;
    private String name;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private ProductStatus status;

    public static PeriodResponse from(ProductPeriod period) {
        return PeriodResponse.builder()
                .id(period.getId())
                .name(period.getName())
                .periodStart(period.getPeriodStart())
                .periodEnd(period.getPeriodEnd())
                .status(period.getStatus())
                .build();
    }
}
