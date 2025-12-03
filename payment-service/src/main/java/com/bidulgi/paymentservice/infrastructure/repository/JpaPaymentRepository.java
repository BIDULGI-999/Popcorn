package com.bidulgi.paymentservice.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidulgi.paymentservice.domain.model.Payment;

public interface JpaPaymentRepository extends JpaRepository<Payment, UUID> {
}
