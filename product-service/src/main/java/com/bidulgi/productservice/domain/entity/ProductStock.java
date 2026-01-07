package com.bidulgi.productservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_product_stock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductStock {

    @Id
    private UUID id;

    private int quantity;

    // 테스트나 초기 생성을 위한 생성자 추가
    public ProductStock(UUID id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public void decrease(int amount) {
        if (quantity < amount) {
            throw new IllegalStateException("재고 부족");
        }
        this.quantity -= amount;
    }
}