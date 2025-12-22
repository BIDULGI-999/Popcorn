package com.bidulgi.paymentservice.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidulgi.paymentservice.domain.model.CompensationLog;

public interface JpaCompensationLogRepository extends JpaRepository<CompensationLog, UUID> {

	boolean existsByPaymentId(UUID paymentId);
	Optional<CompensationLog> findByPaymentId(UUID paymentId);

}
