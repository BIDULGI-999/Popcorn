package com.bidulgi.productservice.presentation.consumer;

import com.bidulgi.productservice.application.service.StockCommandService;
import com.bidulgi.productservice.infrastructure.messaging.kafka.StockDecreaseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockDecreaseConsumer {

    private final StockCommandService stockCommandService;

    @KafkaListener(
            topics = "stock-decrease-requested",
            groupId = "product-service"
    )
    public void consume(StockDecreaseEvent event) {
        stockCommandService.decreaseStockByReservation(event);
    }
}
