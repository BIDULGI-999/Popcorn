package com.bidulgi.productservice.dto.response;

import com.bidulgi.productservice.entity.ReservationSlot;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
public class SlotResponse {
    private UUID id;
    private LocalDate date;
    private LocalTime time;
    private long currentCount;
    private long maxCapacity;
    private boolean isAvailable;

    public static SlotResponse from(ReservationSlot slot) {
        return SlotResponse.builder()
                .id(slot.getId())
                .date(slot.getSlotDate())
                .time(slot.getSlotTime())
                .currentCount(slot.getCurrentCount())
                .maxCapacity(slot.getMaxCapacity())
                .isAvailable(slot.getIsAvailable() && slot.getCurrentCount() < slot.getMaxCapacity())
                .build();
    }
}
