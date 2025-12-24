package com.bidulgi.paymentservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.bidulgi.paymentservice.domain.model.CompensationLog;

public interface CompensationLogRepository {

	boolean existsByPaymentId(UUID paymentId);

	Optional<CompensationLog> findByPaymentId(UUID paymentId);

	CompensationLog save(CompensationLog log);
}
