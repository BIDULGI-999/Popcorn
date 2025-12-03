package com.bidulgi.paymentservice.infrastructure.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.bidulgi.paymentservice.domain.model.PaymentHistory;
import com.bidulgi.paymentservice.domain.repository.PaymentHistoryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentHistoryAdaptor implements PaymentHistoryRepository {

	private final JpaPaymentHistoryRepository jpaPaymentHistoryRepository;

	@Override
	public PaymentHistory save(PaymentHistory paymentHistory) {
		return jpaPaymentHistoryRepository.save(paymentHistory);
	}

	@Override
	public void delete(UUID id, UUID deletedBy) {

	}
}
