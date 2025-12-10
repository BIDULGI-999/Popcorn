package com.bidulgi.productservice.domain.entity;

import com.bidulgi.productservice.domain.entity.constant.ProductStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_product")
public class Product {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "place_id", nullable = false, columnDefinition = "UUID")
    private UUID placeId; // 장소 ID (FK지만, 마이크로서비스/ 느슨한 결합을 위해 ID만 보관)

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // --- 반정규화(캐싱) 필드 ---
    private String address;

    @Column(name = "like_count", columnDefinition = "bigint default 0")
    private Long likeCount;

    @Column(name = "favorite_count", columnDefinition = "bigint default 0")
    private Long favoriteCount;

    @Column(name = "view_count", columnDefinition = "bigint default 0")
    private Long viewCount;

    @Builder
    public Product(UUID placeId, String name, Long price, String description, String address, LocalDateTime startDate, LocalDateTime endDate) {
        this.placeId = placeId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = ProductStatus.PREPARE;
        this.likeCount = 0L;
        this.favoriteCount = 0L;
        this.viewCount = 0L;
    }

    /**
     * 상품 정보 수정 (Dirty Checking 용)
     * null이 들어온 필드는 수정하지 않거나, 정책에 따라 처리
     */
    public void update(String name, Long price, String description, ProductStatus status, String address, LocalDateTime startDate, LocalDateTime endDate) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (price != null) {
            this.price = price;
        }
        if (description != null && !description.isBlank()) {
            this.description = description;
        }
        if (status != null) {
            this.status = status;
        }
        if (address != null) {
            this.address = address;
        }
        if (startDate != null) this.startDate = startDate;
        if (endDate != null) this.endDate = endDate;
    }

    // 좋아요 수 업데이트 (배치 동기화용)
    public void updateLikeCount(Long count) {
        this.likeCount = count;
    }

    public void updateFavoriteCount(Long count) {
        this.favoriteCount = count;
    }

    public void updateViewCount(Long count) {
        this.viewCount = count;
    }
}
