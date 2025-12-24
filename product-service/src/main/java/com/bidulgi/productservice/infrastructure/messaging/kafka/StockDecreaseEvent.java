package com.bidulgi.productservice.infrastructure.messaging.kafka;

import java.util.UUID;

public record StockDecreaseEvent(
        UUID reservationId,
        UUID productStockId,
        UUID reservationSlotId,
        Integer quantity
) {}
