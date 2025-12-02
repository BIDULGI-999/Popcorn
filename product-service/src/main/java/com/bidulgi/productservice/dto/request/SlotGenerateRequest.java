package com.bidulgi.productservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class SlotGenerateRequest {
    @NotNull private LocalDate startDate;
    @NotNull private LocalDate endDate;
    @NotNull private LocalTime startTime; // 예: 10:00
    @NotNull private LocalTime endTime;
    @Min(10) private int intervalMinutes; // 예: 30분 간격
    @Min(1) private long maxCapacity; // 회차당 정원
}
