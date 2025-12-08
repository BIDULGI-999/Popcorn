package com.bidulgi.paymentservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.bidulgi.paymentservice.domain.model.Payment;

public interface PaymentRepository {

	Payment save(Payment payment);

	void delete(UUID paymentId, UUID deletedBy);

	Optional<Payment> findByOrderId(String orderId);

	Optional<Payment> findById(UUID paymentId);
}
