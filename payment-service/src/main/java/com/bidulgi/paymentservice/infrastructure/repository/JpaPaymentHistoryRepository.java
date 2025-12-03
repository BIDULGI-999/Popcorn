package com.bidulgi.paymentservice.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidulgi.paymentservice.domain.model.PaymentHistory;

public interface JpaPaymentHistoryRepository extends JpaRepository<PaymentHistory, UUID> {
}
