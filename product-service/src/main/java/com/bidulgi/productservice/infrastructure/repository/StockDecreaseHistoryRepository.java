package com.bidulgi.productservice.infrastructure.repository;

import com.bidulgi.productservice.domain.entity.StockDecreaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StockDecreaseHistoryRepository extends JpaRepository<StockDecreaseHistory, UUID> {

    boolean existsByReservationId(UUID reservationId);
}
