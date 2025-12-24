package com.bidulgi.productservice.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_decrease_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockDecreaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // DB 레벨에서도 중복 방지
    private UUID reservationId;

    private LocalDateTime processedAt;

    private StockDecreaseHistory(UUID reservationId) {
        this.reservationId = reservationId;
        this.processedAt = LocalDateTime.now();
    }

    public static StockDecreaseHistory of(UUID reservationId) {
        return new StockDecreaseHistory(reservationId);
    }
}