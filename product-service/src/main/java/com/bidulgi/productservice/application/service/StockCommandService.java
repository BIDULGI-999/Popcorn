package com.bidulgi.productservice.application.service;

import com.bidulgi.productservice.domain.entity.ProductStock;
import com.bidulgi.productservice.domain.entity.StockDecreaseHistory;
import com.bidulgi.productservice.infrastructure.messaging.kafka.StockDecreaseEvent;
import com.bidulgi.productservice.infrastructure.repository.ProductStockRepository;
import com.bidulgi.productservice.infrastructure.repository.StockDecreaseHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockCommandService {

    private final ProductStockRepository stockRepository;
    private final StockDecreaseHistoryRepository historyRepository;

    @Transactional
    public void decreaseStockByReservation(StockDecreaseEvent event) {

        // 1️⃣ 멱등성 체크
        if (historyRepository.existsByReservationId(event.reservationId())) {
            return;
        }

        // 2️⃣ 재고 조회 (비관적 락 적용 + 예외 처리 추가)
        ProductStock stock = stockRepository
                .findByIdForUpdate(event.productStockId())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품 재고 정보를 찾을 수 없습니다."));

        // 3️⃣ 도메인 로직
        stock.decrease(event.quantity());

        // 4️⃣ 처리 이력 저장
        historyRepository.save(
                StockDecreaseHistory.of(event.reservationId())
        );
    }
}