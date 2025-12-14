package com.bidulgi.paymentservice.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.bidulgi.paymentservice.domain.model.Payment;
import com.bidulgi.paymentservice.domain.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdaptor implements PaymentRepository {

	private final JpaPaymentRepository jpaPaymentRepository;

	@Override
	public Payment save(Payment payment) {
		return jpaPaymentRepository.save(payment);
	}

	@Override
	public void delete(UUID paymentId, UUID deletedBy) {

	}

	@Override
	public Optional<Payment> findByOrderId(String orderId) {
		return jpaPaymentRepository.findByOrderId(orderId);
	}

	@Override
	public Optional<Payment> findById(UUID paymentId) {
		return jpaPaymentRepository.findById(paymentId);
	}

	@Override
	public Optional<Payment> findByPaymentKey(String paymentKey) {
		return jpaPaymentRepository.findByPaymentKey(paymentKey);
	}
}
