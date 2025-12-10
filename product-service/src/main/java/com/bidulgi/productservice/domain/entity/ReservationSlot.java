package com.bidulgi.productservice.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_reservation_slot",
uniqueConstraints = {
        @UniqueConstraint(
                name = " uq_slot_time",
                columnNames = {"product_period_id", "slot_date", "slot_time"}
        )
})
public class ReservationSlot {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_period_id", nullable = false, columnDefinition = "UUID")
    private ProductPeriod productPeriod;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "slot_time", nullable = false)
    private LocalTime slotTime;

    @Column(nullable = false)
    private Long maxCapacity;

    @Column(nullable = false)
    private Long currentCount;

    @Column(nullable = false)
    private Boolean isAvailable;

    @Version // 낙관적 락 적용
    private Long version;

    @Builder
    public ReservationSlot(ProductPeriod productPeriod, LocalDate slotDate, LocalTime slotTime, Long maxCapacity) {
        this.productPeriod = productPeriod;
        this.slotDate = slotDate;
        this.slotTime = slotTime;
        this.maxCapacity = maxCapacity;
        this.currentCount = 0L;
        this.isAvailable = true;
    }

    // 비즈니스 로직 예시 : 예약 시도
    public void increaseReservation(int count) {
        if (!this.isAvailable || this.currentCount >= this.maxCapacity) {
            throw new IllegalStateException("예약이 불가능하거나 정원이 초과되었습니다.");
        }
//        this.currentCount++;
        this.currentCount += count;

        // 정원 찬 경우 자동으로 예약 불가 전환
        if (this.currentCount >= this.maxCapacity) {
            this.isAvailable = false;
        }
    }

    // 좌석 감소 (재고 복구)
    public void decreaseReservation(int count) {

        if (count < 0) throw new IllegalArgumentException("count는 음수일 수 없습니다.");

        if (this.currentCount < count) {
            throw new IllegalArgumentException("취소 수량이 현재 예약 수보다 많습니다.");
        }
        this.currentCount -= count;


        // 좌석이 생겼다면 다시 예약 가능
        if (this.currentCount < this.maxCapacity) {
            this.isAvailable = true;
        }
    }
}
