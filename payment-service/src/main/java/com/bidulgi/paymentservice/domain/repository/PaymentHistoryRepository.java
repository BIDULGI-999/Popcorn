package com.bidulgi.paymentservice.domain.repository;

import java.util.UUID;

import com.bidulgi.paymentservice.domain.model.PaymentHistory;

public interface PaymentHistoryRepository {

	PaymentHistory save(PaymentHistory paymentHistory);

	void delete(UUID id, UUID deletedBy);
}
