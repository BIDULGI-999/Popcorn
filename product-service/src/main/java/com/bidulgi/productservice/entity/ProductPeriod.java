package com.bidulgi.productservice.entity;

import com.bidulgi.productservice.entity.constant.ProductStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_product_period", indexes = {
        @Index(name = "idx_product_period_product_id", columnList = "product_id")
})
public class ProductPeriod {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, columnDefinition = "BINARY(16)")
    private Product product;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "sale_start_at", nullable = false)
    private LocalDateTime saleStartAt;

    @Column(name = "sale_end_at", nullable = false)
    private LocalDateTime saleEndAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Builder
    public ProductPeriod(Product product, String name, LocalDate periodStart, LocalDate periodEnd, LocalDateTime saleStartAt, LocalDateTime saleEndAt) {
        this.product = product;
        this.name = name;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.saleStartAt = saleStartAt;
        this.saleEndAt = saleEndAt;
        this.status = ProductStatus.PREPARE;
    }
}
