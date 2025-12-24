package com.bidulgi.paymentservice.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.bidulgi.paymentservice.domain.model.CompensationLog;
import com.bidulgi.paymentservice.domain.repository.CompensationLogRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompensationLogAdaptor implements CompensationLogRepository {

	private final JpaCompensationLogRepository jpaCompensationLogRepository;

	@Override
	public boolean existsByPaymentId(UUID paymentId) {
		return jpaCompensationLogRepository.existsByPaymentId(paymentId);
	}

	@Override
	public Optional<CompensationLog> findByPaymentId(UUID paymentId) {
		return jpaCompensationLogRepository.findByPaymentId(paymentId);
	}

	@Override
	public CompensationLog save(CompensationLog log) {
		return jpaCompensationLogRepository.save(log);
	}
}
