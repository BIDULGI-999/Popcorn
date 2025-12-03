package com.bidulgi.productservice.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class UpdateStockRequest {
    @NotNull private UUID slotId;
    @Min(1) private int count;
}
