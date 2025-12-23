package com.bidulgi.productservice.domain.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_product_favorite",
uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_product", columnNames = {"user_id", "product_id"})
})
public class ProductFavorite {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
	private UUID id;

	@Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId; // 회원 시스템 ID (FK 제약 없이 논리적 연결)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, columnDefinition = "UUID")
    private Product product;

    @Builder
    public ProductFavorite(UUID userId, Product product) {
        this.userId = userId;
        this.product = product;
    }
}
